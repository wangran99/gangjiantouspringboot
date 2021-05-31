package com.chinasoft.gangjiantou.service.impl;

import com.chinasoft.gangjiantou.entity.Role;
import com.chinasoft.gangjiantou.mapper.RoleMapper;
import com.chinasoft.gangjiantou.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色定义表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
