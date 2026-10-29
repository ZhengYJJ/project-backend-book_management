package com.library.mapper;

import com.library.entity.BookCopy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookCopyMapper {

    List<BookCopy> selectByPage(@Param("bookId") Long bookId, @Param("status") Integer status,
                                @Param("barcode") String barcode,
                                @Param("offset") int offset, @Param("limit") int limit);

    long count(@Param("bookId") Long bookId, @Param("status") Integer status,
               @Param("barcode") String barcode);

    BookCopy selectById(@Param("id") Long id);

    BookCopy selectByBarcode(@Param("barcode") String barcode);

    int insert(BookCopy copy);

    int updateBarcode(@Param("id") Long id, @Param("barcode") String barcode);

    /** 动态更新：仅更新非 null 字段 */
    int update(BookCopy copy);

    int countInStock(@Param("bookId") Long bookId);

    /** 未注销副本总数（status != 5） */
    int countTotal(@Param("bookId") Long bookId);
}
