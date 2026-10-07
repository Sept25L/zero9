package com.zero9.service;

import com.zero9.domain.model.LoginUser;
import com.zero9.exception.BaseException;
import com.zero9.redis.RedisCache;
import com.zero9.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class TokenService {

    private static final String AUTHORIZATION = "Authorization";

    @Resource
    private RedisCache redisCache;

    public LoginUser getLoginUser(HttpServletRequest request) {

        String token = request.getHeader(AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        token = token.substring(7);

        try {
            // 1. 先校验 JWT 是否有效
            Claims claims = JwtUtils.parseJWT(token);

            // 2. 判断 token 是否过期（JWT 层）
            String username = claims.getSubject();

            // 3. 从 Redis 用 token 取用户（关键点🔥）
            LoginUser loginUser = redisCache.getObject(token, LoginUser.class);

            if (!Objects.equals(username, loginUser.getUsername())) {
                throw new BaseException(401, "登录已过期, 请重新登录!");
            }

            return loginUser;

        } catch (ExpiredJwtException e) {
            throw new BaseException(401, "登录已过期, 请重新登录!");
        } catch (Exception e) {
            throw new BaseException(500, "认证失败: " + e.getMessage());
        }
    }
}
