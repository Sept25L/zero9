package com.zero9.domain.dto;

import com.zero9.domain.PageQuery;
import lombok.Data;

/**
 * TODO：角色分页查询类
 *
 * @author Zero9
 * @version 1.0
 * @BelongsProject zero9
 * @BelongsPackage com.zero9.domain.dto
 * @since 2026-06-14  01:09
 */
@Data
public class RequestRoleDTO  extends PageQuery {
    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限
     */
    private String roleKey;

    /**
     * 角色状态（0正常 1停用）
     */
    private String status;
}
