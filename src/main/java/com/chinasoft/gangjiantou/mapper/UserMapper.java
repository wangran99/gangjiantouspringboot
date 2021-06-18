package com.chinasoft.gangjiantou.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.CcDto;
import com.chinasoft.gangjiantou.dto.UserDto;
import com.chinasoft.gangjiantou.entity.Apply;
import com.chinasoft.gangjiantou.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
public interface UserMapper extends BaseMapper<User> {

    Page<User> queryUser(Page<User> page, UserDto userDto);
}
