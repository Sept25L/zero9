package com.zero9.controller.system.user.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zero9.valid.AddValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RequestUserVO {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户账号
     */
    @NotBlank(message = "用户名不能为空", groups = AddValid.class)
    @Pattern(
            regexp = "^[a-zA-Z0-9]{6,20}$",
            message = "用户名只能包含字母、数字，长度6-20位"
    )
    private String username;

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空", groups = AddValid.class)
    private String password;

    /**
     * 账号状态（0正常 1停用）
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 角色集合
     */
    private List<Long> roles;
}
