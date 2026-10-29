package com.library.service;

import com.library.common.PageResult;
import com.library.entity.Loan;

import java.util.List;

/**
 * 流通服务（FR-CIRC-001 ~ 005）：借书、还书、续借、逾期扫描。
 */
public interface LoanService {

    /** 借书：校验读者状态、借阅上限、逾期未还，通过后建立借阅记录并将副本置为已借出 */
    Loan borrow(String readerNo, String barcode, String remark);

    /** 还书：计算逾期天数与滞纳金，结清借阅记录并将副本置为在馆 */
    Loan returnBook(String barcode, String remark);

    /** 续借：校验续借次数与逾期状态，顺延应还日期 */
    Loan renew(Long loanId);

    PageResult<Loan> page(Long readerId, Integer status, String keyword, int pageNum, int pageSize);

    List<Loan> activeByReader(Long readerId);

    /** 逾期扫描：将在借且超期的记录置为逾期并累计滞纳金，返回处理条数（FR-CIRC-005） */
    int scanOverdue();
}
