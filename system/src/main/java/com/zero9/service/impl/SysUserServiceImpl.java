package com.zero9.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zero9.constant.CommonConstant;
import com.zero9.domain.PageResult;
import com.zero9.domain.SysUser;
import com.zero9.domain.SysUserRole;
import com.zero9.domain.dto.RequestUserDTO;
import com.zero9.mapper.SysRoleMapper;
import com.zero9.mapper.SysUserRoleMapper;
import com.zero9.service.SysUserService;
import com.zero9.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

/**
* @author Zero9
* @description 针对表【sys_user(用户信息表)】的数据库操作Service实现
* @createDate 2026-04-16 15:52:55
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    public boolean existsByUsername(String username) {
        return sysUserMapper.selectCount(
                new QueryWrapper<SysUser>()
                        .eq("username", username)
        ) > 0;
    }

    @Override
    public SysUser getUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        sysUser.setRoles(sysRoleMapper.selectRoleIdByUserId(id));
        return sysUser;
    }

    @Override
    public PageResult<SysUser> getUserList(RequestUserDTO requestUserDTO) {
        Page<SysUser> page = requestUserDTO.toMpPage();

        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class)
                .like(StringUtils.isNotBlank(requestUserDTO.getUsername()),
                        SysUser::getUsername,
                        requestUserDTO.getUsername())
                .like(StringUtils.isNotBlank(requestUserDTO.getPhonenumber()),
                        SysUser::getPhonenumber,
                        requestUserDTO.getPhonenumber())
                .eq(requestUserDTO.getStatus() != null,
                        SysUser::getStatus,
                        requestUserDTO.getStatus());
        IPage<SysUser> result = sysUserMapper.selectPage(page, wrapper);
        return PageResult.build(result);
    }

    /**
     * 新增用户
     * @param user 用户对象
     * @return 新增成功数量
     */
    @Override
    @Transactional
    public int insertUser(SysUser user) {
        int rows = sysUserMapper.insert(user);
        //  新增用户与角色关联
        insertUserRole(user.getId(), user.getRoles());
        return rows;
    }

    /**
     * 修改用户
     * @param user 用户对象
     * @return 修改成功数量
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getId();

        // 删除用户原有的角色关联
        sysUserRoleMapper.deleteUserRoleByUserId(userId);

        // 新增用户当前的角色关联
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            insertUserRole(userId, user.getRoles());
        }

        return sysUserMapper.updateById(user);
    }

    @Override
    @Transactional
    public int deleteUserByIds(List<Long> userIds) {
        //  删除用户与角色关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .in(SysUserRole::getUserId, userIds)
        );
        return sysUserMapper.deleteBatchIds(userIds);
    }

    @Override
    public int updateUserStatus(SysUser user) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();

        wrapper.eq(SysUser::getId, user.getId())
                .set(SysUser::getStatus, user.getStatus())
                .set(SysUser::getUpdateBy, user.getUpdateBy());
        return sysUserMapper.update(wrapper);
    }

    /**
     * 用户与角色关联
     * @param userId 用户id
     * @param roles 角色列表
     */
    public void insertUserRole(Long userId, List<Long> roles) {

        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        List<SysUserRole> list = roles.stream()
                .map(roleId -> {
                    SysUserRole ur = new SysUserRole();
                    ur.setUserId(userId);
                    ur.setRoleId(roleId);
                    return ur;
                })
                .toList();
        sysUserRoleMapper.batchUserRole(list);
    }

    // 验证用户名是否唯一
    public boolean checkUserNameUnique(SysUser user) {
        Long userId = user.getId() == null ? -1L : user.getId();
        SysUser info = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getUsername, user.getUsername())
                        .ne(SysUser::getId, userId)
        );
        if(Objects.nonNull(info)) {
            return CommonConstant.NOT_UNIQUE;
        }
        return CommonConstant.UNIQUE;
    }

    // 验证手机号是否唯一
    public boolean checkPhoneUnique(SysUser user) {
        Long userId = user.getId() == null ? -1L : user.getId();
        SysUser info = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getPhonenumber, user.getPhonenumber())
                        .ne(SysUser::getId, userId)
        );
        if(Objects.nonNull(info)) {
            return CommonConstant.NOT_UNIQUE;
        }
        return CommonConstant.UNIQUE;
    }
    // 验证邮箱是否唯一
    public boolean checkEmailUnique(SysUser user) {
        Long userId = user.getId() == null ? -1L : user.getId();
        SysUser info = sysUserMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getEmail, user.getEmail())
                        .ne(SysUser::getId, userId)
        );
        if(Objects.nonNull(info)) {
            return CommonConstant.NOT_UNIQUE;
        }
        return CommonConstant.UNIQUE;
    }
}




