package com.zero9.domain.model;

import lombok.Data;

@Data
public class LoginBody {
    /**
     * 用户账号
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 验证码
     */
    private String code;
}
