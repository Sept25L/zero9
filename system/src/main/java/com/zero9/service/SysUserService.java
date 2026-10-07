package com.zero9.service;

import com.zero9.domain.PageResult;
import com.zero9.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zero9.domain.dto.RequestUserDTO;

import java.util.List;

/**
* @author Zero9
* @description 针对表【sys_user(用户信息表)】的数据库操作Service
* @CreateDate 2026-04-16 15:52:55
*/
public interface SysUserService extends IService<SysUser> {
    boolean existsByUsername(String username);

    SysUser getUserById(Long id);

    PageResult<SysUser>  getUserList(RequestUserDTO requestUserDTO);

    int insertUser(SysUser user);

    int updateUser(SysUser user);

    int deleteUserByIds(List<Long> userIds);

    int updateUserStatus(SysUser user);

    boolean checkUserNameUnique(SysUser user);

    boolean checkPhoneUnique(SysUser user);

    boolean checkEmailUnique(SysUser user);
}
