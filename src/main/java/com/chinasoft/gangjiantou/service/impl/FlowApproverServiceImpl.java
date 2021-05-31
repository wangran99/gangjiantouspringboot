package com.chinasoft.gangjiantou.service.impl;

import com.chinasoft.gangjiantou.entity.FlowApprover;
import com.chinasoft.gangjiantou.mapper.FlowApproverMapper;
import com.chinasoft.gangjiantou.service.IFlowApproverService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批流程定义默认的审批人表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Service
public class FlowApproverServiceImpl extends ServiceImpl<FlowApproverMapper, FlowApprover> implements IFlowApproverService {

}
