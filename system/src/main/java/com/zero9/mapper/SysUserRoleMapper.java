package com.zero9.mapper;

import com.zero9.domain.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
* @author Zero9
* @description 针对表【sys_user_role(用户-角色表)】的数据库操作Mapper
* @createDate 2026-04-16 15:52:55
* @Entity com.zero9.domain.SysUserRole
*/
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    int batchUserRole(@Param("list") List<SysUserRole> list);

    void deleteUserRoleByUserId(Long userId);
}




