package com.chinasoft.gangjiantou.service.impl;

import com.chinasoft.gangjiantou.entity.Department;
import com.chinasoft.gangjiantou.mapper.DepartmentMapper;
import com.chinasoft.gangjiantou.service.IDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-13
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

}
