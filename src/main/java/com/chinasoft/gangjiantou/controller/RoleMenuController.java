package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinasoft.gangjiantou.dto.RoleMenuDto;
import com.chinasoft.gangjiantou.entity.Role;
import com.chinasoft.gangjiantou.entity.RoleMenu;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.exception.CommonException;
import com.chinasoft.gangjiantou.service.IRoleMenuService;
import com.chinasoft.gangjiantou.service.IRoleService;
import com.chinasoft.gangjiantou.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色菜单对应表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@RestController
@RequestMapping("/role-menu")
public class RoleMenuController {
    @Autowired
    IRoleMenuService roleMenuService;
    @Autowired
    IRoleService roleService;
    @Autowired
    IUserRoleService userRoleService;

    /**
     * 增加新角色和对应的菜单
     *
     * @param roleMenuDto
     * @return
     */
    @PostMapping("add")
    @Transactional
    public boolean add(@RequestBody RoleMenuDto roleMenuDto) {
        Role role = new Role();
        role.setRoleName(roleMenuDto.getRoleName());
        role.setNote(roleMenuDto.getNote());
        roleService.save(role);
        List<RoleMenu> list = new ArrayList<>();
        for (Long menuId : roleMenuDto.getMenuIdList()) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(role.getId());
            list.add(roleMenu);
        }
        roleMenuService.saveBatch(list);
        return true;
    }


    /**
     * 修改绑定角色和菜单
     *
     * @param roleMenuDto
     * @return
     */
    @PostMapping("edit")
    @Transactional
    public boolean edit(@RequestBody RoleMenuDto roleMenuDto) {
        Role role = roleService.getById(roleMenuDto.getRoleId());
        if (role.getStatus() == 0)
            throw new CommonException("该角色禁止编辑");
        roleService.lambdaUpdate().eq(Role::getId, roleMenuDto.getRoleId()).set(Role::getRoleName, roleMenuDto.getRoleName())
                .set(Role::getNote, roleMenuDto.getNote()).update();
        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();
        roleMenuService.remove(wrapper.lambda().eq(RoleMenu::getRoleId, roleMenuDto.getRoleId()));
        List<RoleMenu> roleMenuList = new ArrayList<>();
        for (Long menuId : roleMenuDto.getMenuIdList()) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleMenuDto.getRoleId());
            roleMenu.setMenuId(menuId);
            roleMenuList.add(roleMenu);
        }
        roleMenuService.saveBatch(roleMenuList);
        return true;
    }

    /**
     * 删除角色以及绑定的菜单
     *
     * @param roleId
     * @return
     */
    @PostMapping("del")
    @Transactional
    public boolean delete(Long roleId) {
        Role role = roleService.getById(roleId);
        if (role.getStatus() == 0)
            throw new CommonException("该角色禁止删除");
        roleService.removeById(roleId);
        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();
        roleMenuService.remove(wrapper.lambda().eq(RoleMenu::getRoleId, roleId));
        QueryWrapper<UserRole> wrapper1 = new QueryWrapper<>();
        userRoleService.remove(wrapper1.lambda().eq(UserRole::getRoleId, roleId));
        return true;
    }
}
