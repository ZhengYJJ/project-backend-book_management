package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Reader;
import com.library.entity.ReaderType;
import com.library.mapper.ReaderTypeMapper;
import com.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 读者管理接口（FR-READER-001 ~ 004）。
 */
@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    @Autowired
    private ReaderTypeMapper readerTypeMapper;

    @GetMapping
    public Result<PageResult<Reader>> page(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer status) {
        return Result.ok(readerService.page(keyword, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Reader> detail(@PathVariable Long id) {
        return Result.ok(readerService.detail(id));
    }

    /** 新建读者，自动生成读者证号（R + 6 位序号） */
    @PostMapping
    public Result<Reader> create(@RequestBody Reader reader) {
        return Result.ok(readerService.create(reader));
    }

    @PutMapping("/{id}")
    public Result<Reader> update(@PathVariable Long id, @RequestBody Reader reader) {
        reader.setId(id);
        return Result.ok(readerService.update(reader));
    }

    /** 变更读者状态：0 正常 1 挂失 2 停借 3 注销（FR-READER-003） */
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestBody Reader body) {
        if (body.getStatus() == null) {
            return Result.fail("缺少 status 参数");
        }
        readerService.changeStatus(id, body.getStatus());
        return Result.ok();
    }

    /** 读者类型（借阅规则）列表，供前端下拉选择 */
    @GetMapping("/types")
    public Result<List<ReaderType>> types() {
        return Result.ok(readerTypeMapper.selectAll());
    }
}
