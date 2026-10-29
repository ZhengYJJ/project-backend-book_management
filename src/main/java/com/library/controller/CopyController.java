package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BookCopy;
import com.library.service.CopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 馆藏副本接口（FR-BOOK-003/004/005/006）。
 */
@RestController
@RequestMapping("/api")
public class CopyController {

    @Autowired
    private CopyService copyService;

    /** 某本书下的副本分页列表 */
    @GetMapping("/books/{bookId}/copies")
    public Result<PageResult<BookCopy>> pageByBook(@PathVariable Long bookId,
                                                   @RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                   @RequestParam(required = false) Integer status) {
        return Result.ok(copyService.page(bookId, status, null, pageNum, pageSize));
    }

    /** 按书目批量登记副本，自动生成条码（FR-BOOK-004） */
    @PostMapping("/books/{bookId}/copies")
    public Result<Map<String, Object>> addCopies(@PathVariable Long bookId,
                                                 @RequestBody Map<String, Object> body) {
        int count = 1;
        if (body.get("count") != null) {
            count = Integer.parseInt(String.valueOf(body.get("count")));
        }
        String location = body.get("location") == null ? null : String.valueOf(body.get("location"));
        int added = copyService.addCopies(bookId, count, location);
        return Result.ok(new java.util.LinkedHashMap<String, Object>() {{
            put("bookId", bookId);
            put("added", added);
        }});
    }

    /** 全馆副本分页检索（可按书目/状态/条码过滤） */
    @GetMapping("/copies")
    public Result<PageResult<BookCopy>> page(@RequestParam(required = false) Long bookId,
                                             @RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) String barcode,
                                             @RequestParam(defaultValue = "1") int pageNum,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(copyService.page(bookId, status, barcode, pageNum, pageSize));
    }

    /** 更新副本状态（遗失/修复/注销）、位置与备注 */
    @PutMapping("/copies/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody BookCopy copy) {
        copy.setId(id);
        copyService.update(copy);
        return Result.ok();
    }

    /** 按条码查询副本（借还台扫码辅助） */
    @GetMapping("/copies/barcode/{barcode}")
    public Result<BookCopy> byBarcode(@PathVariable String barcode) {
        return Result.ok(copyService.getByBarcode(barcode));
    }
}
