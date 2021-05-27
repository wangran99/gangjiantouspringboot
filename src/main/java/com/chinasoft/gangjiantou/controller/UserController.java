package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.UserDto;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.entity.UserPosition;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-21
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;
    @Autowired
    IRoleService roleService;
    @Autowired
    IPositionService positionService;
    @Autowired
    IUserPositionService userPositionService;
    @Autowired
    IUserRoleService userRoleService;

    /**
     * 根据条件查询用户
     *
     * @param user
     * @return
     */
    @PostMapping("query")
    public Page<User> query(@RequestBody UserDto user) {
        Page<User> userPage = new Page<>(user.getPageNum(), user.getPageSize());
        Page<User> list = userService.lambdaQuery().eq(true, User::getSex, user.getSex()).
                in(true, User::getDeptCode, user.getDeptList()).like(true, User::getUserNameCn, user.getName()).page(userPage);
        for (User temp : list.getRecords()) {
            temp.setRoleList(userRoleService.lambdaQuery().eq(UserRole::getUserId, temp.getUserId()).list());
            temp.setPositionList(userPositionService.lambdaQuery().eq(UserPosition::getUserId, temp.getUserId()).list());
        }
        return list;
    }
}
