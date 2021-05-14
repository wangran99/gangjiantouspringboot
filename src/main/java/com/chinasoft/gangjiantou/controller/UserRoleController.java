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
     * 查看当前用户的角色
     *
     * @param authCode
     * @return
     */
    @GetMapping("my")
    List<UserRole> getMyRoles(@RequestHeader("authCode") String authCode) {
        String userId = "123";
        return userRoleService.lambdaQuery().eq(UserRole::getUserId, userId).list();
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

    /**
     * 根据部门code和角色id获取人员角色列表
     *
     * @param deptCode
     * @param roleId
     * @return
     */
    @GetMapping("query")
    List<UserRole> getUserRoleList(String deptCode, String roleId) {
        return userRoleService.lambdaQuery().eq(UserRole::getRoleId, roleId).eq(UserRole::getDeptCode, deptCode).list();
    }
}
