package com.chinasoft.gangjiantou.controller;


import com.chinasoft.gangjiantou.entity.Role;
import com.chinasoft.gangjiantou.service.IRoleService;
import com.chinasoft.gangjiantou.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        roleService.removeById(roleId);
        return true;
    }
}
