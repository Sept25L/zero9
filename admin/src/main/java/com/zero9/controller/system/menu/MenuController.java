package com.zero9.controller.system.menu;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zero9.controller.BaseController;
import com.zero9.domain.AjaxResult;
import com.zero9.domain.SysMenu;
import com.zero9.domain.dto.RequestMenuDTO;
import com.zero9.service.SysMenuService;
import com.zero9.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * TODO：功能描述
 *
 * @author Zero9
 * @version 1.0
 * @BelongsProject zero9
 * @BelongsPackage com.zero9.controller.system.menu
 * @since 2026-06-17  00:40
 */
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {

    @Resource
    private SysMenuService sysMenuService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public AjaxResult list(RequestMenuDTO requestMenuDTO) {
        return success(sysMenuService.list(requestMenuDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public AjaxResult getMenu(@PathVariable Long id) {
        return success(sysMenuService.getById(id));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:menu:add')")
    public AjaxResult add(@RequestBody SysMenu sysMenu) {
        AjaxResult result = validate(sysMenu);
        if (result != null) {
            return result;
        }
        return toAjax(sysMenuService.insertMenu(sysMenu));
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public AjaxResult edit(@RequestBody SysMenu sysMenu) {
        AjaxResult result = validate(sysMenu);
        if (result != null) {
            return result;
        }
        return toAjax(sysMenuService.updateMenu(sysMenu));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(sysMenuService.removeMenu(id));
    }

    @GetMapping("/current-menu")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public AjaxResult getMenus() {
        return success(sysMenuService.selectMenuByUserId(SecurityUtils.getLoginUserId()));
    }

    private AjaxResult validate(SysMenu sysMenu) {
        if (StringUtils.isNotEmpty(sysMenu.getMenuName())
                || StringUtils.isNotEmpty(sysMenu.getPath())
                || StringUtils.isNotEmpty(sysMenu.getComponent())
                || StringUtils.isNotEmpty(sysMenu.getPerms())) {

            SysMenu existMenu = sysMenuService.checkMenuUnique(sysMenu);

            if (existMenu != null) {
                if (StringUtils.isNotEmpty(sysMenu.getMenuName())
                        && sysMenu.getMenuName().equals(existMenu.getMenuName())) {
                    return error("新增菜单'" + sysMenu.getMenuName() + "'失败，菜单名称已存在");
                }

                else if (StringUtils.isNotEmpty(sysMenu.getPath())
                        && sysMenu.getPath().equals(existMenu.getPath())) {
                    return error("新增菜单'" + sysMenu.getPath() + "'失败，路由地址已存在");
                }

                else if (StringUtils.isNotEmpty(sysMenu.getComponent())
                        && sysMenu.getComponent().equals(existMenu.getComponent())) {
                    return error("新增菜单'" + sysMenu.getComponent() + "'失败，组件路径已存在");
                }

                else if (StringUtils.isNotEmpty(sysMenu.getPerms())
                        && sysMenu.getPerms().equals(existMenu.getPerms())) {
                    return error("新增菜单'" + sysMenu.getPerms() + "'失败，权限标识已存在");
                }
            }
        }
        return null;
    }

}
