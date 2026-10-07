package com.zero9.mapper;

import com.zero9.domain.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Zero9
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Mapper
* @createDate 2026-04-16 15:52:55
* @Entity com.zero9.domain.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<String> selectPermsByUserId(Long userId);

    List<SysMenu> selectMenuByUserId(Long userId);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    SysMenu checkMenuUnique(SysMenu sysMenu);
}




