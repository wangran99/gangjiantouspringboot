package com.chinasoft.gangjiantou.controller;


import com.chinasoft.gangjiantou.entity.Role;
import com.chinasoft.gangjiantou.entity.RoleMenu;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.exception.CommonException;
import com.chinasoft.gangjiantou.service.IRoleMenuService;
import com.chinasoft.gangjiantou.service.IRoleService;
import com.chinasoft.gangjiantou.service.IUserRoleService;
import com.chinasoft.gangjiantou.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色定义表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-13
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    IRoleService roleService;

    @Autowired
    IUserRoleService userRoleService;

    @Autowired
    IUserService userService;

    @Autowired
    IRoleMenuService roleMenuService;

    /**
     * 获取所有定义的角色
     * @return
     */
    @GetMapping("all")
    List<Role> getRoles(){
       return roleService.list();
    }
    /**
     * 增加新角色
     * @param role
     * @return
     */
    @PostMapping("add")
    boolean add(@RequestBody Role role){
        roleService.save(role);
        return true;
    }

    /**
     * 修改角色
     * @param role
     * @return
     */
    @PostMapping("edit")
    boolean edit(@RequestBody Role role){
        if(role.getStatus()==0)
            throw new CommonException("该角色禁止编辑");
        roleService.updateById(role);
        return true;
    }

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @PostMapping("del")
    boolean delete(Long roleId){
        Role role= roleService.getById(roleId);
        if(role.getStatus()==0)
            throw new CommonException("该角色禁止删除");
        roleService.removeById(roleId);
        return true;
    }

    /**
     * 绑定用户角色
     * @param roleId
     * @param userId
     * @return
     */
    @PostMapping("bind")
    @Transactional
    boolean bind(Long roleId,String userId,List<Long> menuIdList){
        User user=userService.getById(roleId);
        UserRole userRole=new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);
        userRole.setUserName(user.getUserNameCn());
        userRoleService.save(userRole);

        List<RoleMenu> roleMenuList=new ArrayList<>();
        for(Long menuId:menuIdList){
        RoleMenu roleMenu=new RoleMenu();
        roleMenu.setRoleId(roleId);
        roleMenu.setMenuId(menuId);
        roleMenuList.add(roleMenu);
        }
        roleMenuService.saveBatch(roleMenuList);
        return true;
    }


}
