package com.library.interceptor;

import com.library.common.Constants;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录拦截器：/api/**（登录接口除外）须持有有效会话。
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(Constants.SESSION_USER) != null) {
            return true;
        }
        // 返回统一的 401 JSON（HTTP 状态保持 200，由 code 字段区分）
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":401,\"msg\":\"未登录或会话已过期\",\"data\":null}");
        writer.flush();
        return false;
    }
}
