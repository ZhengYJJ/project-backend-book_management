package com.library.service.impl;

import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.common.PageResult;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.service.CopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CopyServiceImpl implements CopyService {

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public PageResult<BookCopy> page(Long bookId, Integer status, String barcode, int pageNum, int pageSize) {
        int size = pageSize <= 0 ? 10 : Math.min(pageSize, 100);
        int offset = Math.max(0, (pageNum - 1) * size);
        String bc = (barcode == null || barcode.trim().isEmpty()) ? null : barcode.trim();
        List<BookCopy> list = bookCopyMapper.selectByPage(bookId, status, bc, offset, size);
        long total = bookCopyMapper.count(bookId, status, bc);
        return PageResult.of(pageNum, size, total, list);
    }

    @Override
    @Transactional
    public int addCopies(Long bookId, int count, String location) {
        if (count <= 0 || count > 100) {
            throw new BusinessException("副本数量须在 1~100 之间");
        }
        if (bookId == null) {
            throw new BusinessException("缺少图书 ID");
        }
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在或已删除");
        }
        for (int i = 0; i < count; i++) {
            BookCopy copy = new BookCopy();
            copy.setBookId(bookId);
            copy.setLocation(location);
            copy.setStatus(Constants.CopyStatus.IN_STOCK);
            bookCopyMapper.insert(copy);
            // 插入后按自增 ID 生成条码：C + 6 位序号（FR-BOOK-004）
            String barcode = String.format("C%06d", copy.getId());
            bookCopyMapper.updateBarcode(copy.getId(), barcode);
        }
        return count;
    }

    @Override
    @Transactional
    public void update(BookCopy copy) {
        if (copy == null || copy.getId() == null) {
            throw new BusinessException("缺少副本 ID");
        }
        BookCopy exists = bookCopyMapper.selectById(copy.getId());
        if (exists == null) {
            throw new BusinessException("副本不存在");
        }
        // 在馆的副本不能手工置为已借出（须走借书流程）
        if (copy.getStatus() != null && copy.getStatus() == Constants.CopyStatus.BORROWED) {
            throw new BusinessException("副本状态不能直接置为已借出，请通过借书流程操作");
        }
        bookCopyMapper.update(copy);
    }

    @Override
    public BookCopy getByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new BusinessException("条码不能为空");
        }
        BookCopy copy = bookCopyMapper.selectByBarcode(barcode.trim());
        if (copy == null) {
            throw new BusinessException("条码不存在：" + barcode);
        }
        return copy;
    }
}
