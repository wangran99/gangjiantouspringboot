package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplyQueryDto {
    /**
     * 流程id
     */
    Long flowId;

    /**
     * 状态（0：待审核 1：已撤回 2：审批中 3：已拒绝 4：审批通过）
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
