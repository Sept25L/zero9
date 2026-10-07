package com.zero9.service.impl;

import com.zero9.domain.SysMenu;
import com.zero9.domain.model.LoginUser;
import com.zero9.domain.SysUser;
import com.zero9.redis.RedisCache;
import com.zero9.service.SysMenuService;
import com.zero9.service.SysUserService;
import com.zero9.utils.JwtUtils;
import com.zero9.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class LoginService {
    @Resource
    private RedisCache redisCache;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysMenuService sysMenuService;

    public HashMap<String, Object> login(String username, String password) {
        HashMap<String, Object> hashMap = new HashMap<>();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String jwt = JwtUtils.createJWT(loginUser.getUsername());
        redisCache.setObject(jwt, loginUser);
        hashMap.put("token", jwt);
        return hashMap;
    }

    public HashMap<String, Object> getUser() {
        HashMap<String, Object> hashMap = new HashMap<>();
        SysUser user = sysUserService.getById(SecurityUtils.getLoginUserId());
        List<SysMenu> sysMenus = sysMenuService.selectMenuByUserId(SecurityUtils.getLoginUserId());
        List<String> perms = sysMenuService.selectPermsByUserId(SecurityUtils.getLoginUserId());
        hashMap.put("user", user);
        hashMap.put("menus", sysMenus);
        hashMap.put("perms", perms);
        return hashMap;
    }

}
