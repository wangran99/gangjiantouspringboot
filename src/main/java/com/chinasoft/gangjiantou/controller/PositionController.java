package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinasoft.gangjiantou.entity.Position;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.entity.UserPosition;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.service.IPositionService;
import com.chinasoft.gangjiantou.service.IUserPositionService;
import com.chinasoft.gangjiantou.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 岗位定义表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@RestController
@RequestMapping("/position")
public class PositionController {

    @Autowired
    IUserService userService;
    @Autowired
    IPositionService positionService;
    @Autowired
    IUserPositionService userPositionService;

    /**
     * 添加岗位
     *
     * @param position
     */
    @PostMapping("add")
    void add(@RequestBody Position position) {
        position.setId(null);
        positionService.save(position);
    }

    /**
     * 修改岗位
     *
     * @param position
     */
    @PostMapping("edit")
    void edit(@RequestBody Position position) {
        positionService.updateById(position);
    }

    /**
     * 删除岗位
     *
     * @param positionId
     */
    @PostMapping("del")
    @Transactional
    boolean del(Long positionId) {
        positionService.removeById(positionId);
        QueryWrapper<UserPosition> wrapper = new QueryWrapper<>();
        userPositionService.remove(wrapper.lambda().in(UserPosition::getPositionId, positionId));
        return true;
    }

    /**
     * 绑定用户和岗位
     *
     * @param userId
     * @param positionIdList
     * @return
     */
    @PostMapping("bind")
    @Transactional
    boolean bind(String userId, List<Long> positionIdList) {
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
        return true;
    }
}
