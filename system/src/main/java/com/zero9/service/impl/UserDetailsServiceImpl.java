package com.zero9.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zero9.domain.model.LoginUser;
import com.zero9.domain.SysUser;
import com.zero9.exception.BaseException;
import com.zero9.service.SysMenuService;
import com.zero9.service.SysRoleService;
import com.zero9.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private SysRoleService sysRoleService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        SysUser sysUser = sysUserService.getOne(
                new QueryWrapper<SysUser>().eq("username", username)
        );
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        } else if ("1".equals(sysUser.getStatus())) {
            throw new BaseException("该账号已封禁, 请联系管理员!");
        }
        return new LoginUser(sysUser, getAuthority(sysUser.getId()));
    }

    private List<GrantedAuthority> getAuthority(Long userId) {

        List<String> roles = sysRoleService.selectRoleByUserId(userId);
        List<String> perms = sysMenuService.selectPermsByUserId(userId);

        return Stream.concat(
                roles.stream()
                    .filter(r -> r != null && !r.isBlank())
                    .map(r -> {
                        String role = r.toUpperCase();
                        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    }),

                perms.stream()
                        .filter(p -> p != null && !p.isBlank())
        )
        .distinct()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    }
}
