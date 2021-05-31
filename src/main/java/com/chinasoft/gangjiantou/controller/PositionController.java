package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.PositionDto;
import com.chinasoft.gangjiantou.entity.Position;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.entity.UserPosition;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.exception.CommonException;
import com.chinasoft.gangjiantou.service.IPositionService;
import com.chinasoft.gangjiantou.service.IUserPositionService;
import com.chinasoft.gangjiantou.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
     * 获取所有的岗位
     *
     * @return
     */
    @GetMapping("all")
    public List<Position> all() {
        return positionService.list();
    }

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
        Position tempPosition = positionService.getById(position.getId());
        if (tempPosition.getEditable() == 0)
            throw new CommonException("该岗位不可编辑/删除");
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
        Position tempPosition = positionService.getById(positionId);
        if (tempPosition.getEditable() == 0)
            throw new CommonException("该岗位不可编辑/删除");
        positionService.removeById(positionId);
        QueryWrapper<UserPosition> wrapper = new QueryWrapper<>();
        userPositionService.remove(wrapper.lambda().in(UserPosition::getPositionId, positionId));
        return true;
    }

    /**
     * 岗位分页查询
     *
     * @param positionDto
     * @return
     */
    @PostMapping("query")
    public Page<Position> query(@RequestBody PositionDto positionDto) {
        Page<Position> positionPage = new Page<>(positionDto.getPageNum(), positionDto.getPageSize());
        return positionService.lambdaQuery().eq(StringUtils.hasText(positionDto.getCode()), Position::getPositionCode, positionDto.getCode())
                .like(StringUtils.hasText(positionDto.getName()), Position::getPositionName, positionDto.getName())
                .eq(positionDto.getStatus() != null, Position::getStatus, positionDto.getStatus()).page(positionPage);
    }

}
