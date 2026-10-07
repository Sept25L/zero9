package com.zero9.domain.dto;

import lombok.Data;

/**
 * TODO：功能描述
 *
 * @author Zero9
 * @version 1.0
 * @BelongsProject zero9
 * @BelongsPackage com.zero9.domain.dto
 * @since 2026-08-30  00:54
 */

@Data
public class RequestMenuDTO {
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 菜单状态（0正常 1停用）
     */
    private String status;
}
