package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplyPendingDto {
    /**
     * 审批单号
     */
    String serialNumber;
    /**
     * 审批单名称
     */
    String subject;
    /**
     * 流程id
     */
    Long flowId;
    /**
     * 申请人
     */
    private String applicant;

    /**
     *
     * 状态（0：待审核 1：审批通过 2：已拒绝 3：转移审批给别人)
     */
    Long status;
    /**
     * 申请开始的时间段
     */
    LocalDateTime startTime;
    /**
     * 申请开始的结束时间段
     */
    LocalDateTime endTime;
    Long pageNum;
    Long pageSize;
}
