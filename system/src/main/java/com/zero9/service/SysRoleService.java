package com.zero9.service;

import com.zero9.domain.PageResult;
import com.zero9.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zero9.domain.SysUser;
import com.zero9.domain.dto.RequestRoleDTO;

import java.util.List;

/**
* @author Zero9
* @description 针对表【sys_role(角色信息表)】的数据库操作Service
* @createDate 2026-04-16 15:52:55
*/
public interface SysRoleService extends IService<SysRole> {

    List<String> selectRoleByUserId(Long userId);

    List<SysRole> getRoles();

    PageResult<SysRole> getRoleList(RequestRoleDTO requestRoleDTO);

    SysRole getRoleById(Long id);

    int insertRole(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleByIds(List<Long> ids);

    boolean checkRoleKeyUnique(SysRole role);

}
