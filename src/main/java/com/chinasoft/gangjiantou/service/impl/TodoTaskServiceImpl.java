package com.chinasoft.gangjiantou.service.impl;

import com.chinasoft.gangjiantou.entity.TodoTask;
import com.chinasoft.gangjiantou.mapper.TodoTaskMapper;
import com.chinasoft.gangjiantou.service.ITodoTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 待办消息表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Service
public class TodoTaskServiceImpl extends ServiceImpl<TodoTaskMapper, TodoTask> implements ITodoTaskService {

}
