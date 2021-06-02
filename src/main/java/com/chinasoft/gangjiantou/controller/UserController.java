package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.BindDto;
import com.chinasoft.gangjiantou.dto.UserDto;
import com.chinasoft.gangjiantou.dto.UserSearchDto;
import com.chinasoft.gangjiantou.entity.Role;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.entity.UserPosition;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.*;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
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
@Slf4j
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
    @Autowired
    IDepartmentService departmentService;
    @Autowired
    RedisService redisService;

    private void getDetails(User user) {
        List<UserRole> userRoleList = userRoleService.lambdaQuery().eq(UserRole::getUserId, user.getUserId()).list();
        userRoleList.stream().forEach(e -> {
            Role role = roleService.getById(e.getRoleId());
            e.setRoleName(roleService.getById(e.getRoleId()).getRoleName());
        });
        user.setRoleList(userRoleList);

        List<UserPosition> userPositionList = userPositionService.lambdaQuery().eq(UserPosition::getUserId, user.getUserId()).list();
        userPositionList.stream().forEach(e -> {
            e.setPositionName(positionService.getById(e.getPositionId()).getPositionName());
        });
        user.setPositionList(userPositionList);
        user.setDepartmentList(departmentService.listByIds(Arrays.asList(user.getDeptCode().split(","))));

    }

    /**
     * 根据部门id获取直属部门下的人员信息
     * @param deptCode
     * @return
     */
    @GetMapping("dept")
    List<User> getUserByDept(String deptCode){
      return   userService.lambdaQuery().like(User::getDeptCode,deptCode).list();
    }

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
            getDetails(temp);
        }
        return list;
    }

    /**
     * 根据岗位和部门查询用户
     *
     * @param userSearchDto
     */
    @PostMapping("search")
    public List<User> search(@RequestBody UserSearchDto userSearchDto) {
        List<User> list1 = userService.lambdaQuery().like(User::getDeptCode, userSearchDto.getDeptCode()).list();
        List<UserPosition> list2 = userPositionService.lambdaQuery().eq(UserPosition::getPositionId, userSearchDto.getPositionId()).list();
        List<User> list = new ArrayList<>();

        for (UserPosition userPosition : list2)
            for (User user : list1)
                if (user.getUserId().equals(userPosition.getUserId())) {
                    list.add(user);
                    break;
                }
        return list;
    }

    /**
     * 根据用户id查询用户信息（包含角色和岗位信息）
     *
     * @param userId
     * @return
     */
    @PostMapping("detail")
    public User detail(String userId) {
        User user = userService.getById(userId);
        getDetails(user);
        return user;
    }

    /**
     * 查询自己的详细信息
     *
     * @param authCode
     * @return
     */
    @GetMapping("my")
    public User my(@RequestHeader("authCode")String authCode) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        User user = userService.getById(userBasicInfoRes.getUserId());
        getDetails(user);
        return user;
    }

    /**
     * 绑定用户和岗位&角色
     *
     * @param bindDto
     * @return
     */
    @PostMapping("bind")
    @Transactional
    boolean bind(@RequestBody BindDto bindDto) {
        String userId = bindDto.getUserId();
        List<Long> positionIdList = bindDto.getPositionIdList();
        List<Long> roleIdList = bindDto.getRoleIdList();

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
        userRoleService.remove(wrapper1.lambda().in(!CollectionUtils.isEmpty(roleIdList), UserRole::getUserId, userId));

        List<UserRole> list1 = new ArrayList<>();
        for (Long roleId : roleIdList) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setUserName(user.getUserNameCn());
            userRole.setRoleId(roleId);
            list1.add(userRole);
        }
        userRoleService.saveBatch(list1);
        return true;
    }
}
