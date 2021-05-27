package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.UserDto;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.service.IUserService;
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

    /**
     * 根据条件查询用户
     *
     * @param user
     * @return
     */
    @PostMapping("query")
    public Page<User> query(@RequestBody UserDto user) {
        Page<User> userPage = new Page<>(user.getPageNum(), user.getPageSize());
        return userService.lambdaQuery().eq(true, User::getSex, user.getSex()).
                in(true, User::getDeptCode, user.getDeptList()).like(true, User::getUserNameCn, user.getName()).page(userPage);
    }
}
