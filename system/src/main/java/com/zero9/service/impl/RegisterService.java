package com.zero9.service.impl;

import com.zero9.domain.SysUser;
import com.zero9.domain.model.RegisterBody;
import com.zero9.service.SysUserService;
import com.zero9.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    @Resource
    private SysUserService sysUserService;

    public String registerUser(RegisterBody registerBody) {

        // 1. 判空
        if (registerBody == null || registerBody.getUsername() == null) {
            return "参数错误";
        }

        String username = registerBody.getUsername();

        // 2. 判断用户名是否已存在
        boolean exists = sysUserService.existsByUsername(username);
        if (exists) {
            return "用户名已存在";
        }

        // 3. 构建用户
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setPassword(
                SecurityUtils.encryptPassword(registerBody.getPassword())
        );

        // 4. 保存
        boolean isSuccess = sysUserService.save(sysUser);

        return isSuccess ? "注册成功!" : "注册失败";
    }
}
