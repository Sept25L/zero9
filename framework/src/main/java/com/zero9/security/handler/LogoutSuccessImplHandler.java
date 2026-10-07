package com.zero9.security.handler;

import com.zero9.domain.AjaxResult;
import com.zero9.domain.model.LoginUser;
import com.zero9.redis.RedisCache;
import com.zero9.service.TokenService;
import com.zero9.utils.JsonUtils;
import com.zero9.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LogoutSuccessImplHandler implements LogoutSuccessHandler {

    @Resource
    private RedisCache redisCache;
    @Resource
    private TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 删除redis缓存信息
        redisCache.delete(SecurityUtils.getToken());
        // 删除当前登录的用户信息
        SecurityUtils.clearLoginUser();
        // 设置响应状态码
        response.setStatus(HttpServletResponse.SC_OK);
        // 设置响应内容为json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 设置字符编码，避免中文乱码
        response.setCharacterEncoding("UTF-8");
        // 响应内容
        String respResult = JsonUtils.toJSONString(AjaxResult.success("退出成功!"));
        response.getWriter().write(respResult);
    }
}
