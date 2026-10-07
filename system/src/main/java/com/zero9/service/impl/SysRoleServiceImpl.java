package com.zero9.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zero9.constant.CommonConstant;
import com.zero9.domain.*;
import com.zero9.domain.dto.RequestRoleDTO;
import com.zero9.mapper.SysMenuMapper;
import com.zero9.mapper.SysRoleMenuMapper;
import com.zero9.service.SysRoleService;
import com.zero9.mapper.SysRoleMapper;
import com.zero9.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
* @author Zero9
* @description 针对表【sys_role(角色信息表)】的数据库操作Service实现
* @createDate 2026-04-16 15:52:55
*/
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
    implements SysRoleService{

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 根据用户id查询所拥有的角色，权限认证使用
     * @param userId 用户id
     * @return roles = {"SuperAdmin"， "Admin", "User"}
     */
    @Override
    public List<String> selectRoleByUserId(Long userId) {
        return sysRoleMapper.selectRoleByUserId(userId);
    }

    /**
     * 新增用户时获取角色列表，无分页
     * @return 角色列表
     */
    @Override
    public List<SysRole> getRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SysRole::getStatus, 0);

        System.out.println(SecurityUtils.getPermissions());
        // 非超级管理员过滤 admin 角色
        if (!SecurityUtils.isAdmin()) {
            wrapper.ne(SysRole::getRoleKey, "admin");
        }

        return sysRoleMapper.selectList(wrapper);
    }

    /**
     * 角色管理-角色列表
     * @param requestRoleDTO 分页查询实体类
     * @return  角色分页查询结果
     */
    @Override
    public PageResult<SysRole> getRoleList(RequestRoleDTO requestRoleDTO) {
        Page<SysRole> page = requestRoleDTO.toMpPage();

        LambdaQueryWrapper<SysRole> wrapper = Wrappers.lambdaQuery(SysRole.class)
                .like(StringUtils.isNotBlank(requestRoleDTO.getRoleName()),
                        SysRole::getRoleName,
                        requestRoleDTO.getRoleName())
                .like(StringUtils.isNotBlank(requestRoleDTO.getRoleKey()),
                        SysRole::getRoleKey,
                        requestRoleDTO.getRoleKey())
                .eq(requestRoleDTO.getStatus() != null,
                        SysRole::getStatus,
                        requestRoleDTO.getStatus());

        if (!SecurityUtils.isAdmin()) {
            wrapper.ne(SysRole::getRoleKey, "admin");
        }

        IPage<SysRole> result = sysRoleMapper.selectPage(page, wrapper);
        return PageResult.build(result);
    }

    @Override
    public SysRole getRoleById(Long id) {
        SysRole sysRole = sysRoleMapper.selectById(id);
        sysRole.setMenus(sysMenuMapper.selectMenuIdsByRoleId(sysRole.getId()));
        return sysRole;
    }

    @Override
    @Transactional
    public int insertRole(SysRole role) {
        int rows = sysRoleMapper.insert(role);
        //  新增角色与菜单关联
        insertRoleMenu(role.getId(), role.getMenus());
        return rows;
    }

    @Override
    @Transactional
    public int updateRole(SysRole role) {
        Long roleId = role.getId();
        //  删除用户与角色关联
        sysRoleMenuMapper.deleteRoleMenuByRoleId(roleId);
        //  新增用户与角色关联
        insertRoleMenu(role.getId(), role.getMenus());
        return sysRoleMapper.updateById(role);
    }

    @Override
    @Transactional
    public int deleteRoleByIds(List<Long> ids) {
        //  删除角色与菜单关联
        sysRoleMenuMapper.delete(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .in(SysRoleMenu::getRoleId, ids)
        );
        return sysRoleMapper.deleteBatchIds(ids);
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        Long roleId = role.getId() == null ? -1L : role.getId();
        SysRole info = sysRoleMapper.selectOne(
                Wrappers.<SysRole>lambdaQuery()
                        .eq(SysRole::getRoleKey, role.getRoleKey())
                        .ne(SysRole::getId, roleId)
        );
        if(Objects.nonNull(info)) {
            return CommonConstant.NOT_UNIQUE;
        }
        return CommonConstant.UNIQUE;
    }

    /**
     * 角色与菜单关联
     * @param roleId 用户id
     * @param menus 菜单列表[1,2]
     */
    public void insertRoleMenu(Long roleId, List<Long> menus) {

        if (CollectionUtils.isEmpty(menus)) {
            return;
        }
        List<SysRoleMenu> list = menus.stream()
                .map(menusId -> {
                    SysRoleMenu rm = new SysRoleMenu();
                    rm.setRoleId(roleId);
                    rm.setMenuId(menusId);
                    return rm;
                })
                .toList();
        sysRoleMenuMapper.batchRoleMenu(list);
    }

}




