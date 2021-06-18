package com.chinasoft.gangjiantou.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.UserDto;
import com.chinasoft.gangjiantou.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
public interface IUserService extends IService<User> {

    Page<User> queryUser(Page<User> page, UserDto userDto);
}
