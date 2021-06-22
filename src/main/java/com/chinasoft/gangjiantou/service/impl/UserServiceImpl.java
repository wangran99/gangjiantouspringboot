package com.chinasoft.gangjiantou.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.UserDto;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.mapper.UserMapper;
import com.chinasoft.gangjiantou.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Page<User> queryUser(Page<User> page, UserDto userDto) {
        return getBaseMapper().queryUser(page,userDto);
    }
}
