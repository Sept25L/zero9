package com.zero9.security.handler;

import com.zero9.domain.AjaxResult;
import com.zero9.utils.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证入口处理类
 * 处理用户未登录访问受保护接口
 */
@Component
public class AnonymousAuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 设置响应状态码
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 设置响应内容为json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 设置字符编码，避免中文乱码
        response.setCharacterEncoding("UTF-8");
        // 响应内容
        String respResult = JsonUtils.toJSONString(AjaxResult.error(401,"未登录，请先登录!"));
        response.getWriter().write(respResult);
    }
}
