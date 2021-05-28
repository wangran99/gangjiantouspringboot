package com.chinasoft.gangjiantou.controller;


import com.chinasoft.gangjiantou.dto.RoleMenuDetailDto;
import com.chinasoft.gangjiantou.dto.RoleMenuDto;
import com.chinasoft.gangjiantou.entity.*;
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
     * 获取所有的角色
     * @return
     */
    @GetMapping("all")
    public List<Role> all(){
        return roleService.list();
    }

    /**
     * 查询定义的角色信息
     *
     * @return
     */
    @PostMapping("query")
    List<RoleMenuDetailDto> query(String roleName) {
        List<Role> roleList = roleService.lambdaQuery().like(true,Role::getRoleName,roleName).list();
        List<RoleMenuDetailDto> roleMenuDetailDtoList = new ArrayList<>();
        for(Role role:roleList){
            RoleMenuDetailDto roleMenuDetailDto=new RoleMenuDetailDto();
            roleMenuDetailDto.setRole(role);
            roleMenuDetailDto.setRoleMenuList(roleMenuService.lambdaQuery().eq(RoleMenu::getRoleId,role.getId()).list());
            roleMenuDetailDtoList.add(roleMenuDetailDto);
        }
        return roleMenuDetailDtoList;
    }



}
