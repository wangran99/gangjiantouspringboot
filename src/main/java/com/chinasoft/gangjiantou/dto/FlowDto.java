package com.chinasoft.gangjiantou.dto;

import com.chinasoft.gangjiantou.entity.ApprovalFlow;
import com.chinasoft.gangjiantou.entity.FlowApprover;
import lombok.Data;

import java.util.List;

@Data
public class FlowDto {
    ApprovalFlow approvalFlow;
    List<FlowApprover> flowApproverList;
}
