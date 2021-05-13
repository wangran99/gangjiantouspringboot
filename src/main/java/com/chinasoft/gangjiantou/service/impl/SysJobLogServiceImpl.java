package com.chinasoft.gangjiantou.service.impl;

import com.chinasoft.gangjiantou.entity.SysJobLog;
import com.chinasoft.gangjiantou.mapper.SysJobLogMapper;
import com.chinasoft.gangjiantou.service.ISysJobLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务调度日志表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-03-24
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements ISysJobLogService {

}
