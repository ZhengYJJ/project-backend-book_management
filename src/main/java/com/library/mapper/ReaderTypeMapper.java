package com.library.mapper;

import com.library.entity.ReaderType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReaderTypeMapper {

    List<ReaderType> selectAll();

    ReaderType selectById(@Param("id") Long id);
}
