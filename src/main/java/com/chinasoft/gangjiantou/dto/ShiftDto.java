package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShiftDto {
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
     * 申请文件的名称
     */
    private String fileName;

    /**
     *
     * 转交审批人的审批状态列表（0：待我审核 1：我已审批通过)
     */
    Integer status;
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
