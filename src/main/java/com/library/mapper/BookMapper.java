package com.library.mapper;

import com.library.entity.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMapper {

    List<Book> selectByPage(@Param("keyword") String keyword, @Param("category") String category,
                            @Param("offset") int offset, @Param("limit") int limit);

    long count(@Param("keyword") String keyword, @Param("category") String category);

    Book selectById(@Param("id") Long id);

    Book selectByIsbn(@Param("isbn") String isbn);

    int insert(Book book);

    int update(Book book);

    /** 软删除 */
    int deleteById(@Param("id") Long id);
}
