package com.library.mapper;

import com.library.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {

    SysUser selectByUsername(@Param("username") String username);

    SysUser selectById(@Param("id") Long id);

    List<SysUser> selectList();

    int insert(SysUser user);

    int updatePassword(@Param("id") Long id, @Param("password") String password);

    int updateLastLoginTime(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
