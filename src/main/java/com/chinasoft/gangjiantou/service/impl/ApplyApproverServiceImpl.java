package com.chinasoft.gangjiantou.service.impl;

import com.chinasoft.gangjiantou.entity.ApplyApprover;
import com.chinasoft.gangjiantou.mapper.ApplyApproverMapper;
import com.chinasoft.gangjiantou.service.IApplyApproverService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批过程经过的审批人(包含转发审批人)表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Service
public class ApplyApproverServiceImpl extends ServiceImpl<ApplyApproverMapper, ApplyApprover> implements IApplyApproverService {

}
