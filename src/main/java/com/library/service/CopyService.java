package com.library.service;

import com.library.common.PageResult;
import com.library.entity.BookCopy;

/**
 * 馆藏副本服务（FR-BOOK-003/004/005/006）。
 */
public interface CopyService {

    PageResult<BookCopy> page(Long bookId, Integer status, String barcode, int pageNum, int pageSize);

    /** 批量登记副本并自动生成条码（C + 6 位序号），返回登记数量 */
    int addCopies(Long bookId, int count, String location);

    /** 更新副本状态/位置/备注 */
    void update(BookCopy copy);

    BookCopy getByBarcode(String barcode);
}
