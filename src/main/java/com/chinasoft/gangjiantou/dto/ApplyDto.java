package com.chinasoft.gangjiantou.dto;

import com.chinasoft.gangjiantou.entity.Apply;
import com.chinasoft.gangjiantou.entity.ApplyApprover;
import lombok.Data;

import java.util.List;

@Data
public class ApplyDto {
    /**
     * 审批概要
     */
    Apply apply;
    /**
     * 每隔审批人的审批详情
     */
    List<ApplyApprover> applyApproverList;
}
