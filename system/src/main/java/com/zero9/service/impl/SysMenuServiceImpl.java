package com.zero9.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zero9.constant.CommonConstant;
import com.zero9.domain.SysMenu;
import com.zero9.domain.SysUser;
import com.zero9.domain.dto.RequestMenuDTO;
import com.zero9.mapper.SysRoleMenuMapper;
import com.zero9.service.SysMenuService;
import com.zero9.mapper.SysMenuMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
* @author Zero9
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service实现
* @createDate 2026-04-16 15:52:55
*/
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService{

    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 根据用户id查询所拥有的权限
     * @param userId 用户id
     * @return perms = {"system:user:list"， "system:user:add", "system:menu:list"}
     */
    @Override
    public List<String> selectPermsByUserId(Long userId) {
        return sysMenuMapper.selectPermsByUserId(userId);
    }

    @Override
    public List<SysMenu> selectMenuByUserId(Long userId) {
        List<SysMenu> menus = sysMenuMapper.selectMenuByUserId(userId);
        return buildMenuTree(menus);
    }

    @Override
    public List<SysMenu> list(RequestMenuDTO requestMenuDTO) {
        LambdaQueryWrapper<SysMenu> wrapper = Wrappers.lambdaQuery(SysMenu.class)
                .like(StringUtils.isNotBlank(requestMenuDTO.getMenuName()),
                        SysMenu::getMenuName,
                        requestMenuDTO.getMenuName())
                .like(StringUtils.isNotBlank(requestMenuDTO.getStatus()),
                        SysMenu::getStatus,
                        requestMenuDTO.getStatus());
        List<SysMenu> menus = sysMenuMapper.selectList(wrapper);
        return buildMenuTree(menus);
    }

    public Integer insertMenu(SysMenu sysMenu) {
        return sysMenuMapper.insert(sysMenu);
    }

    public Integer updateMenu(SysMenu sysMenu) {
        return sysMenuMapper.updateById(sysMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer removeMenu(Long id) {

        // 查询子菜单
        List<SysMenu> children = sysMenuMapper.selectList(
                Wrappers.<SysMenu>lambdaQuery()
                        .eq(SysMenu::getParentId, id)
        );

        // 递归删除子菜单
        for (SysMenu child : children) {
            removeMenu(child.getId());
        }

        // 删除角色-菜单关联
        sysRoleMenuMapper.deleteRoleMenuByMenuId(id);

        // 删除菜单
        return sysMenuMapper.deleteById(id);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {

        Map<Long, SysMenu> map = new HashMap<>();
        List<SysMenu> roots = new ArrayList<>();

        // 初始化 children + 建 map
        for (SysMenu menu : menus) {
            if (menu.getChildren() == null) {
                menu.setChildren(new ArrayList<>());
            }
            map.put(menu.getId(), menu);
        }

        // 构建树
        for (SysMenu menu : menus) {
            Long parentId = menu.getParentId();

            if (parentId == null || parentId == 0L) {
                roots.add(menu);
            } else {
                SysMenu parent = map.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(menu);
                } else {
                    roots.add(menu); // 找不到父节点兜底
                }
            }
        }

        return roots;
    }

    // 批量验证菜单是否唯一
    @Override
    public SysMenu checkMenuUnique(SysMenu sysMenu) {
        return sysMenuMapper.checkMenuUnique(sysMenu);

    }
}




