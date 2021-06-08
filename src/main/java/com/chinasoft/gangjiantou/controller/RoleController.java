package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.RoleDto;
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
import org.springframework.util.StringUtils;
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
    Page<Role> query(@RequestBody RoleDto roleDto) {
        Page<Role> rolePage = new Page<>(roleDto.getPageNum(), roleDto.getPageSize());

        Page<Role>  rolePage1 = roleService.lambdaQuery().like(StringUtils.hasText(roleDto.getRoleName()),Role::getRoleName,roleDto.getRoleName()).page(rolePage);
        for(Role role:rolePage1.getRecords())
            role.setRoleMenuList(roleMenuService.lambdaQuery().eq(RoleMenu::getRoleId,role.getId()).list());

        return rolePage1;
    }



}
