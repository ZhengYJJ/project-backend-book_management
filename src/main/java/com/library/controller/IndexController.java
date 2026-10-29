package com.library.controller;

import com.library.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查端点（无需登录，返回 JSON，非页面）。
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public Result<Map<String, Object>> index() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("app", "图书管理系统 Book Management System");
        info.put("version", "1.0.0");
        info.put("status", "UP");
        info.put("doc", "登录后访问 /api/** 接口，接口清单见项目 README.md");
        return Result.ok(info);
    }
}
