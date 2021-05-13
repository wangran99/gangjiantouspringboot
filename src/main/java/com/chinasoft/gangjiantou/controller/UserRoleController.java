package com.chinasoft.gangjiantou.controller;


import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 绑定用户部门和角色
     *
     * @param userRole
     * @return
     */
    @PostMapping("bind")
    boolean bind(@RequestBody UserRole userRole) {
        userRoleService.save(userRole);
        return true;
    }

    /**
     * 修改用户角色
     *
     * @param userRole
     * @return
     */
    @PostMapping("edit")
    boolean edit(@RequestBody UserRole userRole) {
        userRoleService.updateById(userRole);
        return true;
    }

    /**
     * 删除用户某个角色
     *
     * @param id
     * @return
     */
    @PostMapping("delete")
    boolean delete(Long id) {
        userRoleService.removeById(id);
        return true;
    }
}
