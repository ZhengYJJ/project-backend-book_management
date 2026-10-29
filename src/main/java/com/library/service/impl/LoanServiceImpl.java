package com.library.service.impl;

import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.common.PageResult;
import com.library.entity.BookCopy;
import com.library.entity.Loan;
import com.library.entity.Reader;
import com.library.entity.ReaderType;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.LoanMapper;
import com.library.mapper.ReaderMapper;
import com.library.mapper.ReaderTypeMapper;
import com.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private static final long ONE_DAY_MS = 24L * 60 * 60 * 1000;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private ReaderMapper readerMapper;

    @Autowired
    private ReaderTypeMapper readerTypeMapper;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    // ==================== 借书（FR-CIRC-001） ====================

    @Override
    @Transactional
    public Loan borrow(String readerNo, String barcode, String remark) {
        if (isBlank(readerNo)) {
            throw new BusinessException("读者证号不能为空");
        }
        if (isBlank(barcode)) {
            throw new BusinessException("图书条码不能为空");
        }
        Reader reader = readerMapper.selectByReaderNo(readerNo.trim());
        if (reader == null) {
            throw new BusinessException("读者不存在：" + readerNo);
        }
        if (reader.getStatus() == null || reader.getStatus() != Constants.ReaderStatus.NORMAL) {
            throw new BusinessException("读者状态为「"
                    + Constants.ReaderStatus.nameOf(reader.getStatus()) + "」，不可借阅");
        }
        ReaderType type = requireReaderType(reader);

        // 校验借阅上限
        int active = loanMapper.countActiveByReaderId(reader.getId());
        if (active >= type.getMaxBooks()) {
            throw new BusinessException("已达可借上限 " + type.getMaxBooks()
                    + " 册（当前在借 " + active + " 册）");
        }
        // 校验逾期未还（FR-CIRC-001：无超期未还）
        int overdue = loanMapper.countOverdueByReaderId(reader.getId());
        if (overdue > 0) {
            throw new BusinessException("该读者有 " + overdue
                    + " 册逾期未还图书，请先归还并缴清滞纳金");
        }
        // 校验副本状态
        BookCopy copy = bookCopyMapper.selectByBarcode(barcode.trim());
        if (copy == null) {
            throw new BusinessException("条码不存在：" + barcode);
        }
        if (copy.getStatus() == null || copy.getStatus() != Constants.CopyStatus.IN_STOCK) {
            throw new BusinessException("该副本当前不可借出（状态："
                    + Constants.CopyStatus.nameOf(copy.getStatus()) + "）");
        }

        Date now = new Date();
        Loan loan = new Loan();
        loan.setReaderId(reader.getId());
        loan.setCopyId(copy.getId());
        loan.setBorrowedAt(now);
        loan.setDueAt(plusDays(now, type.getLoanDays()));
        loan.setRenewCount(0);
        loan.setOverdueDays(0);
        loan.setFine(BigDecimal.ZERO);
        loan.setStatus(Constants.LoanStatus.BORROWED);
        loan.setRemark(remark);
        loanMapper.insert(loan);

        // 副本置为已借出
        BookCopy update = new BookCopy();
        update.setId(copy.getId());
        update.setStatus(Constants.CopyStatus.BORROWED);
        bookCopyMapper.update(update);

        return loanMapper.selectById(loan.getId());
    }

    // ==================== 还书（FR-CIRC-002） ====================

    @Override
    @Transactional
    public Loan returnBook(String barcode, String remark) {
        if (isBlank(barcode)) {
            throw new BusinessException("图书条码不能为空");
        }
        BookCopy copy = bookCopyMapper.selectByBarcode(barcode.trim());
        if (copy == null) {
            throw new BusinessException("条码不存在：" + barcode);
        }
        Loan loan = loanMapper.selectActiveByCopyId(copy.getId());
        if (loan == null) {
            throw new BusinessException("该副本当前没有在借记录");
        }

        Date now = new Date();
        int overdueDays = overdueDaysBetween(loan.getDueAt(), now);
        BigDecimal fine = BigDecimal.ZERO;
        if (overdueDays > 0) {
            ReaderType type = requireReaderType(loan.getReaderId());
            fine = type.getFinePerDay().multiply(BigDecimal.valueOf(overdueDays));
        }
        loanMapper.updateReturn(loan.getId(), now, overdueDays, fine,
                Constants.LoanStatus.RETURNED, remark);

        // 副本回置在馆（遗失/修复等状态不动）
        if (copy.getStatus() != null && copy.getStatus() == Constants.CopyStatus.BORROWED) {
            BookCopy update = new BookCopy();
            update.setId(copy.getId());
            update.setStatus(Constants.CopyStatus.IN_STOCK);
            bookCopyMapper.update(update);
        }
        return loanMapper.selectById(loan.getId());
    }

    // ==================== 续借（FR-CIRC-003） ====================

    @Override
    @Transactional
    public Loan renew(Long loanId) {
        if (loanId == null) {
            throw new BusinessException("缺少借阅记录 ID");
        }
        Loan loan = loanMapper.selectById(loanId);
        if (loan == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (loan.getReturnedAt() != null) {
            throw new BusinessException("该记录已归还");
        }
        // 已逾期的不可续借（FR-CIRC-003）
        if (loan.getStatus() == null || loan.getStatus() != Constants.LoanStatus.BORROWED) {
            throw new BusinessException("该记录已逾期，不可续借，请先归还并缴清滞纳金");
        }
        ReaderType type = requireReaderType(loan.getReaderId());
        if (loan.getRenewCount() == null) {
            loan.setRenewCount(0);
        }
        if (loan.getRenewCount() >= type.getRenewLimit()) {
            throw new BusinessException("已达续借次数上限 " + type.getRenewLimit() + " 次");
        }
        Date newDue = plusDays(loan.getDueAt(), type.getLoanDays());
        loanMapper.updateRenew(loanId, newDue, loan.getRenewCount() + 1);
        return loanMapper.selectById(loanId);
    }

    // ==================== 查询 ====================

    @Override
    public PageResult<Loan> page(Long readerId, Integer status, String keyword, int pageNum, int pageSize) {
        int size = pageSize <= 0 ? 10 : Math.min(pageSize, 100);
        int offset = Math.max(0, (pageNum - 1) * size);
        String kw = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<Loan> list = loanMapper.selectByPage(readerId, status, kw, offset, size);
        long total = loanMapper.count(readerId, status, kw);
        return PageResult.of(pageNum, size, total, list);
    }

    @Override
    public List<Loan> activeByReader(Long readerId) {
        if (readerId == null) {
            throw new BusinessException("缺少读者 ID");
        }
        return loanMapper.selectActiveByReaderId(readerId);
    }

    // ==================== 逾期扫描（FR-CIRC-005） ====================

    @Override
    @Transactional
    public int scanOverdue() {
        Date now = new Date();
        List<Loan> duePassed = loanMapper.selectDuePassed(now);
        int count = 0;
        for (Loan loan : duePassed) {
            ReaderType type = requireReaderType(loan.getReaderId());
            int days = overdueDaysBetween(loan.getDueAt(), now);
            if (days < 1) {
                days = 1;
            }
            BigDecimal fine = type.getFinePerDay().multiply(BigDecimal.valueOf(days));
            loanMapper.updateOverdue(loan.getId(), days, fine);
            count++;
        }
        return count;
    }

    // ==================== 工具方法 ====================

    private ReaderType requireReaderType(Reader reader) {
        return requireReaderType(reader.getId());
    }

    private ReaderType requireReaderType(Long readerId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) {
            throw new BusinessException("读者数据异常（ID=" + readerId + "）");
        }
        ReaderType type = readerTypeMapper.selectById(reader.getTypeId());
        if (type == null) {
            throw new BusinessException("读者「" + reader.getName() + "」的借阅规则配置缺失");
        }
        return type;
    }

    private Date plusDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    /** 计算逾期天数：now 早于等于 dueAt 返回 0，否则按天向上取整 */
    private int overdueDaysBetween(Date dueAt, Date now) {
        long diff = now.getTime() - dueAt.getTime();
        if (diff <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) diff / ONE_DAY_MS);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
