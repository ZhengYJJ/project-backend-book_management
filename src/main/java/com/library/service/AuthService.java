package com.library.service;

import com.library.entity.SysUser;

/**
 * 认证与账号服务（FR-SYS-006 登录安全）。
 */
public interface AuthService {

    /** 登录校验，成功返回用户（不含密码） */
    SysUser login(String username, String password);

    /** 修改当前用户密码 */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
