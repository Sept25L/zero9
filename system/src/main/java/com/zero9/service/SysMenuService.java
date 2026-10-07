package com.zero9.service;

import com.zero9.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zero9.domain.dto.RequestMenuDTO;

import java.util.List;

/**
* @author Zero9
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service
* @createDate 2026-04-16 15:52:55
*/
public interface SysMenuService extends IService<SysMenu> {

    List<String> selectPermsByUserId(Long userId);

    List<SysMenu> selectMenuByUserId(Long userId);

    List<SysMenu> list(RequestMenuDTO requestMenuDTO);

    Integer insertMenu(SysMenu sysMenu);

    Integer updateMenu(SysMenu sysMenu);

    Integer removeMenu(Long id);

    SysMenu checkMenuUnique(SysMenu sysMenu);
}
