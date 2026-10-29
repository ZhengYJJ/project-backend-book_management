package com.library.service.impl;

import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.Book;
import com.library.mapper.BookCopyMapper;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Override
    public PageResult<Book> page(int pageNum, int pageSize, String keyword, String category) {
        int size = normalizeSize(pageSize);
        int offset = Math.max(0, (pageNum - 1) * size);
        List<Book> list = bookMapper.selectByPage(trim(keyword), trim(category), offset, size);
        long total = bookMapper.count(trim(keyword), trim(category));
        return PageResult.of(pageNum, size, total, list);
    }

    @Override
    public Book detail(Long id) {
        return requireBook(id);
    }

    @Override
    @Transactional
    public Book create(Book book) {
        if (book == null || isBlank(book.getTitle())) {
            throw new BusinessException("书名不能为空");
        }
        book.setTitle(book.getTitle().trim());
        // ISBN 查重（FR-BOOK-001）
        if (!isBlank(book.getIsbn())) {
            book.setIsbn(book.getIsbn().trim());
            Book exists = bookMapper.selectByIsbn(book.getIsbn());
            if (exists != null) {
                throw new BusinessException("该 ISBN 已存在书目：" + exists.getTitle());
            }
        }
        bookMapper.insert(book);
        return bookMapper.selectById(book.getId());
    }

    @Override
    @Transactional
    public Book update(Book book) {
        if (book == null || book.getId() == null) {
            throw new BusinessException("缺少图书 ID");
        }
        requireBook(book.getId());
        bookMapper.update(book);
        return bookMapper.selectById(book.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = requireBook(id);
        // 有未注销副本时禁止删除（FR-BOOK-007）
        int copies = bookCopyMapper.countTotal(id);
        if (copies > 0) {
            throw new BusinessException("《" + book.getTitle() + "》仍有 " + copies
                    + " 个馆藏副本，请先注销全部副本");
        }
        bookMapper.deleteById(id);
    }

    private Book requireBook(Long id) {
        if (id == null) {
            throw new BusinessException("缺少图书 ID");
        }
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在或已删除");
        }
        return book;
    }

    private int normalizeSize(int pageSize) {
        if (pageSize <= 0) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }

    private String trim(String s) {
        return s == null ? null : s.trim().isEmpty() ? null : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
