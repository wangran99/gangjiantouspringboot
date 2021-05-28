package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.UserDto;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.entity.UserPosition;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
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
     * 根据条件查询用户详细信息
     *
     * @param user
     * @return
     */
    @PostMapping("query")
    public Page<User> query(@RequestBody UserDto user) {
        Page<User> userPage = new Page<>(user.getPageNum(), user.getPageSize());
        Page<User> list = userService.lambdaQuery().eq(StringUtils.hasText(user.getSex()), User::getSex, user.getSex()).
                in(!CollectionUtils.isEmpty(user.getDeptList()), User::getDeptCode, user.getDeptList())
                .like(StringUtils.hasText(user.getName()), User::getUserNameCn, user.getName()).page(userPage);
        for (User temp : list.getRecords()) {
            temp.setRoleList(userRoleService.lambdaQuery().eq(UserRole::getUserId, temp.getUserId()).list());
            temp.setPositionList(userPositionService.lambdaQuery().eq(UserPosition::getUserId, temp.getUserId()).list());
        }
        return list;
    }

    /**
     * 根据用户id查询用户信息（包含角色和岗位信息）
     * @param userId
     * @return
     */
    @PostMapping("detail")
    public User detail(String userId){
        User user= userService.getById(userId);
        user.setRoleList(userRoleService.lambdaQuery().eq(UserRole::getUserId, userId).list());
        user.setPositionList(userPositionService.lambdaQuery().eq(UserPosition::getUserId, userId).list());
        return user;
    }

    /**
     * 绑定用户和岗位&角色
     *
     * @param userId
     * @param positionIdList
     * @return
     */
    @PostMapping("bind")
    @Transactional
    boolean bind(String userId, List<Long> positionIdList,List<Long> roleIdList) {
        QueryWrapper<UserPosition> wrapper = new QueryWrapper<>();
        userPositionService.remove(wrapper.lambda().in(UserPosition::getUserId, userId));

        User user = userService.getById(userId);
        List<UserPosition> list = new ArrayList<>();
        for (Long positionId : positionIdList) {
            UserPosition userPosition = new UserPosition();
            userPosition.setUserId(userId);
            userPosition.setUserName(user.getUserNameCn());
            userPosition.setPositionId(positionId);
            list.add(userPosition);
        }
        userPositionService.saveBatch(list);

        QueryWrapper<UserRole> wrapper1 = new QueryWrapper<>();
        userRoleService.remove(wrapper1.lambda().in(UserRole::getRoleId, roleIdList));

        List<UserRole> list1=new ArrayList<>();
        for(Long roleId:roleIdList){
            UserRole userRole=new UserRole();
            userRole.setUserId(userId);
            userRole.setUserName(user.getUserNameCn());
            userRole.setRoleId(roleId);
            list1.add(userRole);
        }
        userRoleService.saveBatch(list1);
        return true;
    }
}
