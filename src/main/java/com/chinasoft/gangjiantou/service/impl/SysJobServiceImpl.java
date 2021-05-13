package com.chinasoft.gangjiantou.service.impl;

import com.chinasoft.gangjiantou.entity.SysJob;
import com.chinasoft.gangjiantou.mapper.SysJobMapper;
import com.chinasoft.gangjiantou.service.ISysJobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务调度表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-03-24
 */
@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements ISysJobService {

}
