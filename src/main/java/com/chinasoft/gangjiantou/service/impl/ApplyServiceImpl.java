package com.chinasoft.gangjiantou.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.ApplyPendingDto;
import com.chinasoft.gangjiantou.entity.Apply;
import com.chinasoft.gangjiantou.mapper.ApplyMapper;
import com.chinasoft.gangjiantou.service.IApplyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 申请表 服务实现类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Service
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply> implements IApplyService {

    @Override
    public Page<Apply> pendingApply(Page<Apply> page,String userId, ApplyPendingDto applyPendingDto) {
        return getBaseMapper().pendingApply(page,userId,applyPendingDto);
    }

    @Override
    public Page<Apply> queryCC(Page<Apply> page, String userId, ApplyPendingDto applyPendingDto) {
        return getBaseMapper().queryCC(page,userId,applyPendingDto);
    }
}
