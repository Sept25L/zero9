package com.zero9.controller.system.role;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zero9.controller.BaseController;
import com.zero9.domain.AjaxResult;
import com.zero9.domain.SysRole;
import com.zero9.domain.SysUser;
import com.zero9.domain.dto.RequestRoleDTO;
import com.zero9.service.SysRoleService;
import com.zero9.utils.SecurityUtils;
import com.zero9.valid.AddValid;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台角色接口
 */
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Resource
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    public AjaxResult getRoles(RequestRoleDTO requestRoleDTO) {
        return success(sysRoleService.getRoleList(requestRoleDTO));
    }

    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/current-user")
    public AjaxResult getCurrentUserRoles() {
        return success(
                sysRoleService.getRoles()
        );
    }

    /**
     * 根据id获取角色信息
     * @param id 角色id
     * @return  角色信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public AjaxResult getUser(@PathVariable Long id) {
        return success(sysRoleService.getRoleById(id));
    }

    /**
     * 新增角色
     * @param role 角色实体类
     * @return  成功或者失败
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:role:add')")
    public AjaxResult add(@RequestBody SysRole role) {
        AjaxResult result = validate(role);
        if (result != null) {
            return result;
        }
        role.setUpdateBy(SecurityUtils.getLoginUserName());
        return toAjax(sysRoleService.insertRole(role));
    }

    /**
     * 新增角色
     * @param role 角色实体类
     * @return  成功或者失败
     */
    @PutMapping("/edit")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public AjaxResult edit(@RequestBody SysRole role) {
        AjaxResult result = validate(role);
        if (result != null) {
            return result;
        }
        role.setUpdateBy(SecurityUtils.getLoginUserName());
        return toAjax(sysRoleService.updateRole(role));
    }

    @DeleteMapping("/{roleIds}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public AjaxResult remove(@PathVariable List<Long> roleIds) {
        if (roleIds != null && roleIds.contains(1L)) {
            return error("当前用户不能删除");
        }
        return toAjax(sysRoleService.deleteRoleByIds(roleIds));
    }

    private AjaxResult validate(SysRole role) {
        /*
          sysRoleService.checkRoleKeyUnique(sysRole.getRoleKey())
          如果为true那么就是唯一
         */
        if((!sysRoleService.checkRoleKeyUnique(role))) {
            return error("修改角色'" + role.getRoleName() + "'失败，角色标识已存在");
        }
        return null;
    }

}
