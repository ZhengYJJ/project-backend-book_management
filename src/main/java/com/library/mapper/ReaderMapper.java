package com.library.mapper;

import com.library.entity.Reader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReaderMapper {

    List<Reader> selectByPage(@Param("keyword") String keyword, @Param("status") Integer status,
                              @Param("offset") int offset, @Param("limit") int limit);

    long count(@Param("keyword") String keyword, @Param("status") Integer status);

    Reader selectById(@Param("id") Long id);

    Reader selectByReaderNo(@Param("readerNo") String readerNo);

    int insert(Reader reader);

    int updateReaderNo(@Param("id") Long id, @Param("readerNo") String readerNo);

    /** 动态更新：仅更新非 null 字段 */
    int update(Reader reader);

    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
