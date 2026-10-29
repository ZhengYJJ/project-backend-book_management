package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 书目管理接口（FR-BOOK-001/002/007，FR-SEARCH-001 组合检索）。
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    /** 组合检索：keyword 匹配书名/作者/ISBN，category 为中图法分类号 */
    @GetMapping
    public Result<PageResult<Book>> page(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String category) {
        return Result.ok(bookService.page(pageNum, pageSize, keyword, category));
    }

    @GetMapping("/{id}")
    public Result<Book> detail(@PathVariable Long id) {
        return Result.ok(bookService.detail(id));
    }

    @PostMapping
    public Result<Book> create(@RequestBody Book book) {
        return Result.ok(bookService.create(book));
    }

    @PutMapping("/{id}")
    public Result<Book> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        return Result.ok(bookService.update(book));
    }

    /** 软删除（有馆藏副本时拒绝） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.ok();
    }
}
