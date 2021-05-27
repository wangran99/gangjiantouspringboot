package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinasoft.gangjiantou.entity.RoleMenu;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.IUserRoleService;
import com.chinasoft.gangjiantou.service.IUserService;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户角色表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-13
 */
@RestController
@RequestMapping("/user-role")
public class UserRoleController {

    @Autowired
    IUserRoleService userRoleService;

    @Autowired
    RedisService redisService;

    @Autowired
    IUserService userService;

    private UserBasicInfoRes getUserBasicInfoRes(String authCode) {
        return redisService.getUserInfo(authCode);
    }


    /**
     * 获取当前用户的角色
     *
     * @param authCode
     * @return
     */
    @GetMapping("my")
    List<UserRole> getMyRoles(@RequestHeader("authCode") String authCode) {
        UserBasicInfoRes userBasicInfoRes=redisService.getUserInfo(authCode);
        return userRoleService.lambdaQuery().eq(UserRole::getUserId, userBasicInfoRes.getUserId()).list();
    }

    /**
     * 获取用户角色列表
     *
     * @return
     */
    @GetMapping("userid")
    List<UserRole> getUserRoleList(String userId) {
        return userRoleService.lambdaQuery().eq(UserRole::getUserId, userId).list();
    }


    /**
     * 绑定用户和角色
     * @param userId
     * @param roleIdList
     * @return
     */
    @PostMapping("bind")
    @Transactional
    boolean bind(String userId,List<Long> roleIdList ){
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        userRoleService.remove(wrapper.lambda().in(UserRole::getRoleId, roleIdList));

        User user= userService.getById(userId);
        List<UserRole> list=new ArrayList<>();
        for(Long roleId:roleIdList){
            UserRole userRole=new UserRole();
            userRole.setUserId(userId);
            userRole.setUserName(user.getUserNameCn());
            userRole.setRoleId(roleId);
            list.add(userRole);
        }
        userRoleService.saveBatch(list);
        return true;
    }
}
