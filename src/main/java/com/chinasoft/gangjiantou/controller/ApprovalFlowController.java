package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.FlowDto;
import com.chinasoft.gangjiantou.dto.FlowQueryDto;
import com.chinasoft.gangjiantou.entity.ApprovalFlow;
import com.chinasoft.gangjiantou.entity.FlowApprover;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.IApprovalFlowService;
import com.chinasoft.gangjiantou.service.IDepartmentService;
import com.chinasoft.gangjiantou.service.IFlowApproverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    IFlowApproverService flowApproverService;
    @Autowired
    IDepartmentService departmentService;
    @Autowired
    RedisService redisService;

    /**
     * 添加审批流程模型
     *
     * @param fLowDto
     */
    @PostMapping("add")
    @Transactional
    boolean add(@RequestBody FlowDto fLowDto) {
        ApprovalFlow approvalFlow = fLowDto.getApprovalFlow();
        approvalFlow.setId(null);
        approvalFlowService.save(approvalFlow);
        fLowDto.getFlowApproverList().forEach(e -> {
            e.setId(null);
            e.setFlowId(approvalFlow.getId());
        });
        flowApproverService.saveBatch(fLowDto.getFlowApproverList());
        return true;
    }

    /**
     * 根据ID获取审批流程模型
     *
     * @param flowId
     */
    @GetMapping("add")
    FlowDto add(@PathVariable("flowId") Long flowId) {
        FlowDto flowDto = new FlowDto();
        flowDto.setApprovalFlow(approvalFlowService.getById(flowId));
        flowDto.setFlowApproverList(flowApproverService.lambdaQuery().eq(FlowApprover::getFlowId, flowDto).list());
        return flowDto;
    }

    /**
     * 编辑审批流程模型
     *
     * @param fLowDto
     */
    @PostMapping("edit")
    @Transactional
    boolean edit(@RequestBody FlowDto fLowDto) {
        ApprovalFlow approvalFlow = fLowDto.getApprovalFlow();
        flowApproverService.remove(new QueryWrapper<FlowApprover>().lambda()
                .eq(FlowApprover::getFlowId, approvalFlow.getId()));

        approvalFlowService.updateById(approvalFlow);
        fLowDto.getFlowApproverList().forEach(e -> {
            e.setId(null);
            e.setFlowId(approvalFlow.getId());
        });
        flowApproverService.saveBatch(fLowDto.getFlowApproverList());
        return true;
    }

    /**
     * 删除审批流程模型
     *
     * @param flowId 流程模型id
     */
    @PostMapping("del")
    @Transactional
    boolean edit(Long flowId) {
        approvalFlowService.removeById(flowId);
        flowApproverService.remove(new QueryWrapper<FlowApprover>().lambda()
                .eq(FlowApprover::getFlowId, flowId));
        return true;
    }

    /**
     * 分页查询审批流程模型
     *
     * @param flowQueryDto
     */
    @PostMapping("query")
    Page<ApprovalFlow> edit(FlowQueryDto flowQueryDto) {
        Page<ApprovalFlow> page = new Page<>(flowQueryDto.getPageNumber(), flowQueryDto.getPageSize());
        Page<ApprovalFlow> approvalFlowPage = approvalFlowService.lambdaQuery()
                .like(StringUtils.hasText(flowQueryDto.getFlowName()), ApprovalFlow::getFlowName, flowQueryDto.getFlowName())
                .eq(flowQueryDto.getDeptCode() != null, ApprovalFlow::getDeptCode, flowQueryDto.getDeptCode()).page(page);
        approvalFlowPage.getRecords().forEach(e -> {
            e.setFlowName(departmentService.getById(e.getId()).getDeptNameCn());
        });
        return approvalFlowPage;
    }
}
