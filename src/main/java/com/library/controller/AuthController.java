package com.library.controller;

import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.common.Result;
import com.library.entity.SysUser;
import com.library.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 认证接口（FR-SYS-006）。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** 登录（无需登录即可访问，见 spring-mvc.xml 拦截器配置） */
    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody Map<String, String> body, HttpSession session) {
        SysUser user = authService.login(body.get("username"), body.get("password"));
        session.setAttribute(Constants.SESSION_USER, user);
        return Result.ok(user);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }

    @GetMapping("/me")
    public Result<SysUser> me(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute(Constants.SESSION_USER);
        return Result.ok(user);
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute(Constants.SESSION_USER);
        if (user == null) {
            throw new BusinessException("会话已失效，请重新登录");
        }
        authService.changePassword(user.getId(), body.get("oldPassword"), body.get("newPassword"));
        return Result.ok();
    }
}
