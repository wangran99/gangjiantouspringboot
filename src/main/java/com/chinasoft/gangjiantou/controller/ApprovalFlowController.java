package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.FlowDto;
import com.chinasoft.gangjiantou.dto.FlowQueryDto;
import com.chinasoft.gangjiantou.entity.ApprovalFlow;
import com.chinasoft.gangjiantou.entity.FlowApprover;
import com.chinasoft.gangjiantou.entity.Position;
import com.chinasoft.gangjiantou.exception.CommonException;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.IApprovalFlowService;
import com.chinasoft.gangjiantou.service.IDepartmentService;
import com.chinasoft.gangjiantou.service.IFlowApproverService;
import com.chinasoft.gangjiantou.service.IPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
    IPositionService positionService;
    @Autowired
    RedisService redisService;

    /**
     * 使流程生效
     * @param flowId
     * @return
     */
    @PostMapping("available")
    boolean ok(Long flowId) {
      ApprovalFlow approvalFlow= approvalFlowService.getById(flowId);
       approvalFlow.setStatus(1);
       approvalFlowService.getById(approvalFlow);
        return true;
    }

    /**
     * 使流程不生效
     * @param flowId
     * @return
     */
    @PostMapping("inavailable")
    boolean noOk(Long flowId) {
        ApprovalFlow approvalFlow= approvalFlowService.getById(flowId);
        approvalFlow.setStatus(0);
        approvalFlowService.getById(approvalFlow);
        return true;
    }

    /**
     * 添加审批流程模型
     *
     * @param flowDto
     */
    @PostMapping("add")
    @Transactional
    boolean add(@RequestBody FlowDto flowDto) {
        if (CollectionUtils.isEmpty(flowDto.getFlowApproverList()))
            throw new CommonException("审批人不能为空");
        ApprovalFlow approvalFlow = flowDto.getApprovalFlow();
        approvalFlow.setId(null);
        approvalFlowService.save(approvalFlow);
        flowDto.getFlowApproverList().forEach(e -> {
            e.setId(null);
            e.setFlowId(approvalFlow.getId());
        });
        flowApproverService.saveBatch(flowDto.getFlowApproverList());
        return true;
    }

    /**
     * 根据ID获取审批流程模型
     *
     * @param flowId
     */
    @GetMapping("detail/{flowId}")
    FlowDto detail(@PathVariable("flowId") Long flowId) {
        FlowDto flowDto = new FlowDto();
        ApprovalFlow approvalFlow = approvalFlowService.getById(flowId);
        approvalFlow.setDeptName(departmentService.getById(approvalFlow.getDeptCode()).getDeptNameCn());
        approvalFlow.setPositionName(positionService.getById(approvalFlow.getPositionId()).getPositionName());
        flowDto.setApprovalFlow(approvalFlow);

        flowDto.setFlowApproverList(flowApproverService.lambdaQuery().eq(FlowApprover::getFlowId, flowId).list());

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
    boolean del(Long flowId) {
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
    Page<ApprovalFlow> query(@RequestBody FlowQueryDto flowQueryDto) {
        Page<ApprovalFlow> page = new Page<>(flowQueryDto.getPageNumber(), flowQueryDto.getPageSize());
        Page<ApprovalFlow> approvalFlowPage = approvalFlowService.lambdaQuery()
                .like(StringUtils.hasText(flowQueryDto.getFlowName()), ApprovalFlow::getFlowName, flowQueryDto.getFlowName())
                .eq(StringUtils.hasText(flowQueryDto.getDeptCode()), ApprovalFlow::getDeptCode, flowQueryDto.getDeptCode()).page(page);
        approvalFlowPage.getRecords().forEach(e -> {
            e.setDeptName(departmentService.getById(e.getDeptCode()).getDeptNameCn());
        });
        return approvalFlowPage;
    }
}
