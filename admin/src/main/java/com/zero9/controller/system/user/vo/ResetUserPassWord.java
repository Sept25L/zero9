package com.zero9.controller.system.user.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO：功能描述
 *
 * @author Zero9
 * @version 1.0
 * @BelongsProject zero9
 * @BelongsPackage com.zero9.controller.system.user.vo
 * @since 2026-09-01  21:37
 */
@Data
public class ResetUserPassWord {
    private Long id;
    @NotBlank(message = "密码不能为空")
    private String oldPassWord;
    @NotBlank(message = "密码不能为空")
    private String newPassWord;
    @NotBlank(message = "密码不能为空")
    private String confirmPassWord;
}
