package com.library.controller;

import com.library.common.Result;
import com.library.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计接口（FR-RPT-001/002）。
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    /** 借阅排行：days 不传则统计全部时间，limit 默认 10 */
    @GetMapping("/top-borrowed")
    public Result<List<Map<String, Object>>> topBorrowed(
            @RequestParam(required = false) Integer days,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.ok(statsService.topBorrowed(days, limit));
    }

    /** 近 N 个月借还统计，默认 6 个月 */
    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> monthly(
            @RequestParam(defaultValue = "6") int months) {
        return Result.ok(statsService.monthlyCount(months));
    }
}
