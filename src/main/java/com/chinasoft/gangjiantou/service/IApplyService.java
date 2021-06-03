package com.chinasoft.gangjiantou.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.ApplyPendingDto;
import com.chinasoft.gangjiantou.dto.CcDto;
import com.chinasoft.gangjiantou.entity.Apply;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 申请表 服务类
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
public interface IApplyService extends IService<Apply> {
    Page<Apply> pendingApply(Page<Apply> page, String userId, ApplyPendingDto applyPendingDto);

    Page<Apply> queryCC(Page<Apply> page, String userId, CcDto ccDto);

}
