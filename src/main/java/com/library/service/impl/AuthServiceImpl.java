package com.library.service.impl;

import com.library.common.BusinessException;
import com.library.entity.SysUser;
import com.library.mapper.SysUserMapper;
import com.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public SysUser login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            throw new BusinessException("用户名和密码不能为空");
        }
        SysUser user = sysUserMapper.selectByUsername(username.trim());
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 0) {
            throw new BusinessException("账号已停用，请联系管理员");
        }
        sysUserMapper.updateLastLoginTime(user.getId());
        // 返回前清除密码
        user.setPassword(null);
        return user;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (isBlank(oldPassword) || isBlank(newPassword)) {
            throw new BusinessException("原密码和新密码不能为空");
        }
        if (newPassword.trim().length() < 6) {
            throw new BusinessException("新密码长度至少 6 位");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        sysUserMapper.updatePassword(userId, passwordEncoder.encode(newPassword.trim()));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
