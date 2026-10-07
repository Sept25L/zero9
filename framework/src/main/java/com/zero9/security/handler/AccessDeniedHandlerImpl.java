package com.zero9.security.handler;

import com.zero9.domain.AjaxResult;
import com.zero9.utils.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 设置响应状态码
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // 设置响应内容为json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 设置字符编码，避免中文乱码
        response.setCharacterEncoding("UTF-8");
        // 响应内容
        String respResult = JsonUtils.toJSONString(AjaxResult.error(403,"权限不足, 请联系管理员!"));
        response.getWriter().write(respResult);
    }
}
