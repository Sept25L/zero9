package com.zero9.mapper;

import com.zero9.domain.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Zero9
* @description 针对表【sys_role(角色信息表)】的数据库操作Mapper
* @createDate 2026-04-16 15:52:55
* @Entity com.zero9.domain.SysRole
*/
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<String> selectRoleByUserId(Long userId);

    List<Long> selectRoleIdByUserId(Long userId);
}




