package com.chinasoft.gangjiantou.controller;



import com.chinasoft.gangjiantou.dto.FlowDto;
import com.chinasoft.gangjiantou.entity.ApprovalFlow;
import com.chinasoft.gangjiantou.service.IApprovalFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 审批流程定义表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@RestController
@RequestMapping("/approval-flow")
public class ApprovalFlowController {

    @Autowired
    IApprovalFlowService approvalFlowService;

    @PostMapping("add")
    void add(@RequestBody FlowDto fLowDto){

    }
}
