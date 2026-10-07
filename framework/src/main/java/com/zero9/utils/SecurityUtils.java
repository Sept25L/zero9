package com.zero9.utils;

import com.zero9.domain.model.LoginUser;
import com.zero9.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 安全工具类
 */
public class SecurityUtils {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 获取token
     * @return  请求的token信息
     */
    public static String getToken() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return token;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getLoginUserName() {
        return getLoginUser().getUsername();
    }

    /**
     * 获取权限集合
     */
    public static Set<String> getPermissions() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getAuthorities() == null) {
            return Set.of();
        }

        return loginUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    // =========================
    // 权限判断核心方法
    // =========================

    /**
     * 是否拥有某个权限
     */
    public static boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }

    /**
     * 是否拥有某个角色
     * 统一使用 ROLE_ 前缀
     */
    public static boolean hasRole(String role) {
        return getPermissions().contains("ROLE_" + role);
    }

    /**
     * 是否拥有任意角色
     */
    public static boolean hasAnyRole(String... roles) {
        Set<String> perms = getPermissions();

        for (String role : roles) {
            if (perms.contains("ROLE_" + role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否超级管理员
     * 👉 推荐：用角色判断，而不是 userId=1
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 是否登录
     */
    public static boolean isLogin() {
        return getLoginUser() != null;
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BaseException("用户未登录");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof LoginUser loginUser)) {
            throw new BaseException("登录信息异常");
        }

        return loginUser;
    }

    /**
     * 获取用户ID
     */
    public static Long getLoginUserId() {
        return getLoginUser().getUser().getId();
    }

    /**
     * 清除登录信息
     */
    public static void clearLoginUser() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 获取认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 加密密码
     */
    public static String encryptPassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * 密码校验
     */
    public static boolean matches(String raw, String encoded) {
        return PASSWORD_ENCODER.matches(raw, encoded);
    }
}