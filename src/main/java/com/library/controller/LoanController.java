package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Loan;
import com.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 流通接口（FR-CIRC-001 ~ 005）：借书、还书、续借、逾期扫描。
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    /** 借书：{ "readerNo": "R000001", "barcode": "C000001", "remark": "可选" } */
    @PostMapping("/borrow")
    public Result<Loan> borrow(@RequestBody Map<String, String> body) {
        return Result.ok(loanService.borrow(body.get("readerNo"), body.get("barcode"), body.get("remark")));
    }

    /** 还书：{ "barcode": "C000001", "remark": "可选" }，自动计算逾期与滞纳金 */
    @PostMapping("/return")
    public Result<Loan> returnBook(@RequestBody Map<String, String> body) {
        return Result.ok(loanService.returnBook(body.get("barcode"), body.get("remark")));
    }

    /** 续借（FR-CIRC-003） */
    @PostMapping("/{id}/renew")
    public Result<Loan> renew(@PathVariable Long id) {
        return Result.ok(loanService.renew(id));
    }

    /** 借阅记录分页查询：status 0在借 1已还 2逾期；keyword 匹配读者/证号/条码/书名 */
    @GetMapping
    public Result<PageResult<Loan>> page(@RequestParam(required = false) Long readerId,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(loanService.page(readerId, status, keyword, pageNum, pageSize));
    }

    /** 某读者的在借（含逾期）列表（FR-READER-005） */
    @GetMapping("/active")
    public Result<List<Loan>> active(@RequestParam Long readerId) {
        return Result.ok(loanService.activeByReader(readerId));
    }

    /** 手动触发逾期扫描（正常由定时任务每日 01:00 执行，此处用于测试/补扫） */
    @PostMapping("/scan-overdue")
    public Result<Integer> scanOverdue() {
        return Result.ok(loanService.scanOverdue());
    }
}
