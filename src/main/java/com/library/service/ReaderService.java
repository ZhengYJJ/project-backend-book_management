package com.library.service;

import com.library.common.PageResult;
import com.library.entity.Reader;

/**
 * 读者管理服务（FR-READER-001 ~ 004）。
 */
public interface ReaderService {

    PageResult<Reader> page(String keyword, Integer status, int pageNum, int pageSize);

    Reader detail(Long id);

    /** 新建读者并自动生成读者证号（R + 6 位序号） */
    Reader create(Reader reader);

    Reader update(Reader reader);

    /** 变更读者状态（挂失/停借/注销等） */
    void changeStatus(Long id, int status);
}
