package com.library.service;

import com.library.common.PageResult;
import com.library.entity.Book;

/**
 * 书目管理服务（FR-BOOK-001/002/007）。
 */
public interface BookService {

    PageResult<Book> page(int pageNum, int pageSize, String keyword, String category);

    Book detail(Long id);

    Book create(Book book);

    Book update(Book book);

    void delete(Long id);
}
