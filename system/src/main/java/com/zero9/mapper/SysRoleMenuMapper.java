package com.zero9.mapper;

import com.zero9.domain.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zero9.domain.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Zero9
* @description 针对表【sys_role_menu(角色-菜单表)】的数据库操作Mapper
* @createDate 2026-04-16 15:52:55
* @Entity com.zero9.domain.SysRoleMenu
*/
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
    int batchRoleMenu(@Param("list") List<SysRoleMenu> list);

    void deleteRoleMenuByRoleId(Long userId);
    void deleteRoleMenuByMenuId(Long menuId);

}




