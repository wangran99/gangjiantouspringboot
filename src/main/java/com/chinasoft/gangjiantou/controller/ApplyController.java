package com.chinasoft.gangjiantou.controller;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinasoft.gangjiantou.dto.*;
import com.chinasoft.gangjiantou.entity.*;
import com.chinasoft.gangjiantou.exception.CommonException;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.*;
import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.AddTodoTaskReq;
import com.github.wangran99.welink.api.client.openapi.model.AddTodoTaskRes;
import com.github.wangran99.welink.api.client.openapi.model.SendOfficialAccountMsgReq;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * <p>
 * 申请表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@RestController
@Slf4j
@RequestMapping("/apply")
public class ApplyController {

    private String url = "";
    @Autowired
    IUserService userService;
    @Autowired
    IApplyService applyService;
    @Autowired
    IFlowApproverService flowApproverService;
    @Autowired
    IPositionService positionService;
    @Autowired
    IApprovalFlowService approvalFlowService;
    @Autowired
    IUserPositionService userPositionService;
    @Autowired
    IFileService fileService;
    @Autowired
    IApplyApproverService applyApproverService;
    @Autowired
    ICarbonCopyService carbonCopyService;
    @Autowired
    IDepartmentService departmentService;

    @Autowired
    ITodoTaskService todoTaskService;

    @Autowired
    IRoleService roleService;

    @Autowired
    RedisService redisService;
    @Autowired
    OpenAPI openAPI;

//    @GetMapping("test")
//    @Transactional
//    public void test(){
//        for(int i=0;i<10;i++){
//        Role role=new Role();
//        role.setRoleName("qqqq");
//        role.setNote("aaaaa");
//        role.setStatus(0);
//        roleService.save(role);}
//
//    }


    /**
     * 获取我能发起申请的列表
     *
     * @param authCode
     * @return
     */
    @GetMapping("myFlow")
    List<ApprovalFlow> getMyFlow(@RequestHeader("authCode") String authCode) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        List<UserPosition> userPositionList = userPositionService.lambdaQuery().eq(UserPosition::getUserId, userBasicInfoRes.getUserId()).list();
        List<ApprovalFlow> approvalFlowList = approvalFlowService.lambdaQuery().in(ApprovalFlow::getDeptCode, userBasicInfoRes.getDeptCodes())
                .eq(ApprovalFlow::getStatus, 1).in(ApprovalFlow::getPositionId, userPositionList.stream().map(e -> e.getPositionId())
                        .collect(Collectors.toList())).list();
        approvalFlowList.forEach(e -> e.setDeptName(departmentService.getById(e.getDeptCode()).getDeptNameCn()));
        return approvalFlowList;
    }

    /**
     * 新增申请
     *
     * @return
     */
    @PostMapping("add")
    @Transactional
    public boolean add(@RequestHeader("authCode") String authCode, @RequestBody Apply apply) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        apply.setId(null);
        apply.setApplicantId(userBasicInfoRes.getUserId());
        apply.setApplicant(userBasicInfoRes.getUserNameCn());
        //生成流水号
        LocalDateTime localDateTime = LocalDateTime.now();
        ThreadLocalRandom threadRandom = ThreadLocalRandom.current();
        Long randomLong = threadRandom.nextLong(0L, Integer.MAX_VALUE);
        String serialNumber = String.format("%d%02d%02d", localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth())
                + randomLong;
        apply.setSerialNumber(serialNumber);

        ApprovalFlow approvalFlow = approvalFlowService.getById(apply.getFlowId());
        List<FlowApprover> flowApproverList = flowApproverService.lambdaQuery().eq(FlowApprover::getFlowId, approvalFlow.getId()).orderByAsc(FlowApprover::getId).list();
//        log.error("flowapproverlist:" + flowApproverList.toString());
        apply.setCurrentApproverId(flowApproverList.get(0).getUserId());
        apply.setCurrentApprover(flowApproverList.get(0).getUserName());

        applyService.save(apply);

        List<ApplyApprover> applyApproverList = new ArrayList<>();
        flowApproverList.forEach(e -> {
            ApplyApprover applyApprover = new ApplyApprover();
            applyApprover.setApplyId(apply.getId());
            applyApprover.setApproverId(e.getUserId());
            applyApprover.setApproverName(e.getUserName());
            Position position = positionService.getById(e.getPositionId());
            applyApprover.setPositionName(position != null ? position.getPositionName() : "");
            applyApproverList.add(applyApprover);
        });
        //插入审批人
        for (int i = 0; i < applyApproverList.size(); i++) {
            ApplyApprover approver = applyApproverList.get(i);
            approver.setApplyId(apply.getId());
            if ((i + 1) < applyApproverList.size()) {
                ApplyApprover next = applyApproverList.get(i + 1);
                approver.setNextApproverId(next.getApproverId());
                approver.setNextApproverName(next.getApproverName());
            }
        }
//        log.error(" approverlist:" + applyApproverList.toString());
        applyApproverService.saveBatch(applyApproverList);
        if (CollectionUtils.isNotEmpty(apply.getFileList()))
            fileService.lambdaUpdate().in(File::getId, apply.getFileList().stream().map(e -> e.getId()).collect(Collectors.toList()))
                    .set(File::getApplyId, apply.getId()).update();
        if (apply.getCcList() != null)
            for (String userId : apply.getCcList()) {
                CarbonCopy carbonCopy = new CarbonCopy();
                carbonCopy.setApplyId(apply.getId());
                carbonCopy.setUserId(userId);
                carbonCopy.setUserName(userService.getById(userId).getUserNameCn());
                carbonCopy.setCreateUserId(userBasicInfoRes.getUserId());
                carbonCopy.setCreateUserName(userBasicInfoRes.getUserNameCn());
                carbonCopyService.save(carbonCopy);
            }
        addTodoTask(apply);
        return true;
    }

    /**
     * 分页查询我的申请
     *
     * @param authCode
     * @param applyDto
     * @return
     */
    @PostMapping("query")
    Page<Apply> query(@RequestHeader("authCode") String authCode, @RequestBody ApplyQueryDto applyDto) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        Page<Apply> page = new Page<>(applyDto.getPageNum(), applyDto.getPageSize());
        Page<Apply> applyPage = applyService.lambdaQuery().eq(Apply::getApplicantId, userBasicInfoRes.getUserId())
                .eq(applyDto.getFlowId() != null, Apply::getFlowId, applyDto.getFlowId())
                .eq(applyDto.getStatus() != null, Apply::getStatus, applyDto.getStatus())
                .ge(applyDto.getStartTime() != null, Apply::getApplyTime, applyDto.getStartTime())
                .le(applyDto.getEndTime() != null, Apply::getApplyTime, applyDto.getEndTime())
                .orderByDesc(Apply::getApplyTime).page(page);
        applyPage.getRecords().forEach(e -> {
            ApprovalFlow approvalFlow = approvalFlowService.getById(e.getFlowId());
            e.setFlowName(approvalFlow.getFlowName());
            e.setDeptName(departmentService.getById(approvalFlow.getDeptCode()).getDeptNameCn());
        });
        return applyPage;
    }

    /**
     * 分页查询待我审批的申请
     *
     * @param authCode
     * @param applyPendingDto
     * @return
     */
    @PostMapping("pending")
    Page<Apply> pending(@RequestHeader("authCode") String authCode, @RequestBody ApplyPendingDto applyPendingDto) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        Page<Apply> page = new Page<>(applyPendingDto.getPageNum(), applyPendingDto.getPageSize());
        Page<Apply> applyPage = applyService.pendingApply(page, userBasicInfoRes.getUserId(), applyPendingDto);
        applyPage.getRecords().forEach(e -> {
            ApprovalFlow approvalFlow = approvalFlowService.getById(e.getFlowId());
            e.setFlowName(approvalFlow.getFlowName());
            e.setDeptName(departmentService.getById(approvalFlow.getDeptCode()).getDeptNameCn());
        });
        return applyPage;
    }


    /**
     * 根据id获取申请详情
     *
     * @param authCode
     * @param id       申请的id
     */
    @GetMapping("detail/{id}")
    ApplyDto detail(@RequestHeader("authCode") String authCode, @PathVariable("id") Long id) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        Apply apply = applyService.getById(id);
        List<ApplyApprover> applyApproverUnsortedList = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, apply.getId())
                .orderByAsc(ApplyApprover::getId).list();

        List<ApplyApprover> applyApproverList = new ArrayList<>();
        ApplyApprover applyApprover = applyApproverUnsortedList.get(0);
        applyApproverList.add(applyApprover);
        while (applyApprover.getNextApproverId() != null) {
            ApplyApprover finalApplyApprover = applyApprover;
            List<ApplyApprover> tempList = applyApproverUnsortedList.stream()
                    .filter(e -> e.getApproverId().equals(finalApplyApprover.getNextApproverId())).collect(Collectors.toList());
            applyApprover = tempList.get(0);
            applyApproverList.add(applyApprover);
        }
        List<CarbonCopy> carbonCopyList = carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, id).list();
        List<String> aviableUserIdList = new ArrayList<>();
        aviableUserIdList.add(apply.getApplicantId());
        aviableUserIdList.addAll(applyApproverList.stream().map(e -> e.getApproverId()).collect(Collectors.toList()));
        aviableUserIdList.addAll(carbonCopyList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        if (!aviableUserIdList.contains(userBasicInfoRes.getUserId()))
            throw new CommonException("你无权查看别人的审批流程");
        //获取抄送人员列表
        apply.setCcList(carbonCopyList.stream().map(e -> e.getUserName()).collect(Collectors.toList()));
        ApprovalFlow approvalFlow = approvalFlowService.getById(apply.getFlowId());
        apply.setFlowName(approvalFlow.getFlowName());
        apply.setDeptName(departmentService.getById(approvalFlow.getDeptCode()).getDeptNameCn());
        apply.setFileList(fileService.lambdaQuery().eq(File::getApplyId, id).eq(File::getSource, -1).list());

        ApplyDto applyDto = new ApplyDto();
        applyDto.setApply(apply);
        applyDto.setApplyApproverList(applyApproverList);
        applyDto.getApplyApproverList().forEach(e -> e.setFileList(fileService.lambdaQuery().eq(File::getApplyId, id)
                .eq(File::getApprovalId, e.getApproverId()).list()));
        return applyDto;
    }

    /**
     * 审批通过
     *
     * @param authCode
     * @param approvalDto
     * @return
     */
    @PostMapping("ok")
    @Transactional
    public boolean ok(@RequestHeader("authCode") String authCode, @RequestBody ApprovalDto approvalDto) {
        UserBasicInfoRes user = redisService.getUserInfo(authCode);
        Apply apply = applyService.getById(approvalDto.getApplyId());
        if (apply == null)
            throw new CommonException("该审批不存在");
        if (apply.getStatus() == 1)
            throw new CommonException("该审批已经撤回");
        if (!apply.getCurrentApproverId().equals(user.getUserId()))
            throw new CommonException("当前审批人未审核结束");
        if (apply.getEndTime() != null)
            throw new CommonException("当前审批已经结束");

        ApplyApprover applyApprover = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, apply.getId())
                .eq(ApplyApprover::getApproverId, user.getUserId()).one();
        applyApprover.setStatus(1);
        applyApprover.setComment(approvalDto.getComment());
        applyApprover.setFileComment(approvalDto.getFileComment());
        applyApproverService.updateById(applyApprover);
        delTodoTaskByApplyId(apply.getId());
        if (applyApprover.getNextApproverId() == null) {
            apply.setStatus(4);
            apply.setEndTime(LocalDateTime.now());
            applyService.updateById(apply);
            SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
            sendOfficialAccountMsgReq.setMsgContent(apply.getApplicant() + "的文件审批通过");
            sendOfficialAccountMsgReq.setMsgOwner("文件审批者");
            sendOfficialAccountMsgReq.setUrlType("html");
            sendOfficialAccountMsgReq.setUrlPath(url + "/#/detail?id=" + apply.getId());
            List<String> list = new ArrayList<>();
            list.add(apply.getApplicantId());
            List<CarbonCopy> carbonCopyList = carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, apply.getId()).list();
            list.addAll(carbonCopyList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
            sendOfficialAccountMsgReq.setToUserList(list);
            sendOfficialAccountMsgReq.setMsgTitle("文件审批");
            sendOfficialAccountMsgReq.setMsgRange("0");
            openAPI.sendOfficialAccountMsg(sendOfficialAccountMsgReq);
        } else {
            apply.setCurrentApproverId(applyApprover.getNextApproverId());
            apply.setCurrentApprover(applyApprover.getNextApproverName());
            apply.setStatus(2);
            applyService.updateById(apply);
            addTodoTask(apply);
        }
        return true;
    }

    /**
     * 审批拒绝
     *
     * @param authCode
     * @param approvalDto
     * @return
     */
    @PostMapping("reject")
    @Transactional
    public boolean reject(@RequestHeader("authCode") String authCode, @RequestBody ApprovalDto approvalDto) {
        UserBasicInfoRes user = redisService.getUserInfo(authCode);
        Apply apply = applyService.getById(approvalDto.getApplyId());
        if (apply == null)
            throw new CommonException("该审批不存在");
        if (apply.getStatus() == 1)
            throw new CommonException("该审批已经撤回");
        if (!apply.getCurrentApproverId().equals(user.getUserId()))
            throw new CommonException("当前审批人未审核结束");
        if (apply.getEndTime() != null)
            throw new CommonException("当前审批已经结束");
        apply.setStatus(2);
        apply.setEndTime(LocalDateTime.now());
        applyService.updateById(apply);

        ApplyApprover applyApprover = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, apply.getId())
                .eq(ApplyApprover::getApproverId, user.getUserId()).one();
        applyApprover.setStatus(2);
        applyApprover.setComment(approvalDto.getComment());
        applyApprover.setFileComment(approvalDto.getFileComment());
        applyApproverService.updateById(applyApprover);
        delTodoTaskByApplyId(apply.getId());

        SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
        sendOfficialAccountMsgReq.setMsgContent(apply.getApplicant() + "的文件审核被驳回,点击查看详情");
        sendOfficialAccountMsgReq.setMsgOwner("文件审批者");
        sendOfficialAccountMsgReq.setUrlType("html");
        sendOfficialAccountMsgReq.setUrlPath(url + "/#/detail?id=" + apply.getId());
        List<String> list = new ArrayList<>();
        list.add(apply.getApplicantId());
        list.addAll(carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, apply.getId()).list()
                .stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        sendOfficialAccountMsgReq.setToUserList(list);
        sendOfficialAccountMsgReq.setMsgTitle("文件审批");
        sendOfficialAccountMsgReq.setMsgRange("0");
        openAPI.sendOfficialAccountMsg(sendOfficialAccountMsgReq);
        return true;
    }

    /**
     * 转给其他人审批
     *
     * @param authCode
     * @param approvalDto
     * @return
     */
    @PostMapping("shift")
    @Transactional
    public boolean shiftApprover(@RequestBody ApprovalDto approvalDto, @RequestHeader("authCode") String authCode) {
        UserBasicInfoRes user = redisService.getUserInfo(authCode);
        Apply apply = applyService.getById(approvalDto.getApplyId());
        if (apply == null)
            throw new CommonException("审批不存在");
        if (apply.getStatus() == 1)
            throw new CommonException("该审批已经撤回");
        if (!apply.getCurrentApproverId().equals(user.getUserId()))
            throw new CommonException("当前审批人未审核结束");
        if (apply.getEndTime() != null)
            throw new CommonException("当前审批已经结束");
        User shiftUser = userService.getById(approvalDto.getShiftUserId());
        ApplyApprover applyApprover = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, apply.getId())
                .eq(ApplyApprover::getApproverId, user.getUserId()).one();
        applyApprover.setStatus(3);
        applyApprover.setComment(approvalDto.getComment());
        applyApprover.setFileComment(approvalDto.getFileComment());

        ApplyApprover newApplyApprover = new ApplyApprover();
        newApplyApprover.setApplyId(approvalDto.getApplyId());
        newApplyApprover.setApproverId(shiftUser.getUserId());
        newApplyApprover.setPositionName("");
        newApplyApprover.setApproverName(shiftUser.getUserNameCn());
        newApplyApprover.setNextApproverId(applyApprover.getNextApproverId());
        newApplyApprover.setNextApproverName(applyApprover.getNextApproverName());
        applyApproverService.save(newApplyApprover);

        applyApprover.setNextApproverId(shiftUser.getUserId());
        applyApprover.setNextApproverName(shiftUser.getUserNameCn());
        applyApproverService.updateById(applyApprover);

        apply.setCurrentApprover(shiftUser.getUserNameCn());
        apply.setCurrentApproverId(shiftUser.getUserId());
        applyService.updateById(apply);

        if (CollectionUtils.isNotEmpty(approvalDto.getCcList())) {
            List<CarbonCopy> carbonCopyList = new ArrayList<>();
            approvalDto.getCcList().forEach(e -> {
                CarbonCopy carbonCopy = new CarbonCopy();
                carbonCopy.setApplyId(apply.getId());
                carbonCopy.setUserId(e);
                carbonCopy.setUserName(userService.getById(e).getUserNameCn());
                carbonCopy.setCreateUserId(user.getUserId());
                carbonCopy.setCreateUserName(user.getUserNameCn());
                carbonCopyList.add(carbonCopy);
            });
            carbonCopyService.saveBatch(carbonCopyList);
        }
        delTodoTaskByApplyId(apply.getId());
        addTodoTask(apply);
        return true;
    }

    /**
     * 撤回审批请求
     *
     * @param applyId  申请id
     * @param authCode
     * @return
     */
    @PostMapping("recall")
    @Transactional
    public boolean recallApply(Long applyId, @RequestHeader("authCode") String authCode) {
        UserBasicInfoRes user = redisService.getUserInfo(authCode);
        Apply apply = applyService.getById(applyId);
        if (!apply.getApplicantId().equals(user.getUserId()))
            throw new CommonException("您无权撤回别人的申请");
        if (apply.getEndTime() != null)
            throw new CommonException("当前审批已经结束");
        if (apply.getStatus() != 0)
            throw new CommonException("当前审批正在进行中，不能撤回");
        apply.setStatus(1);
        apply.setRecallTime(LocalDateTime.now());
        apply.setEndTime(LocalDateTime.now());
        applyService.updateById(apply);
        //删除所有待办
        delTodoTaskByApplyId(applyId);
        return true;
    }

    /**
     * 分页查询抄送我的审批
     *
     * @param authCode
     * @param ccDto
     */
    @PostMapping("querycc")
    Page<Apply> querycc(@RequestHeader("authCode") String authCode, @RequestBody CcDto ccDto) {
        UserBasicInfoRes user = redisService.getUserInfo(authCode);
        Page<Apply> page = new Page<>(ccDto.getPageNum(), ccDto.getPageSize());
        Page<Apply> applyPage = applyService.queryCC(page, user.getUserId(), ccDto);
        applyPage.getRecords().forEach(e -> {
            ApprovalFlow approvalFlow = approvalFlowService.getById(e.getFlowId());
            e.setFlowName(approvalFlow.getFlowName());
            e.setDeptName(departmentService.getById(approvalFlow.getDeptCode()).getDeptNameCn());
        });
        return applyPage;
    }

    private void delTodoTaskByApplyId(Long applyId) {
        List<TodoTask> list = todoTaskService.lambdaQuery().eq(TodoTask::getApplyId, applyId).list();
        for (TodoTask todoTask : list)
            openAPI.delTodoTask(todoTask.getTaskId());
        todoTaskService.remove(Wrappers.<TodoTask>lambdaQuery().eq(TodoTask::getApplyId, applyId));
    }

    //发送welink审批待办消息提醒
    private void addTodoTask(Apply apply) {
        TodoTask todoTask = new TodoTask();
        //发送待办任务提醒
        String taskId = UUID.randomUUID().toString().replaceAll("-", "");
        todoTask.setTaskId(taskId);
        todoTask.setApplyId(apply.getId());
        todoTask.setUserId(apply.getCurrentApproverId());
        todoTask.setUserName(apply.getCurrentApprover());
        todoTaskService.save(todoTask);

        AddTodoTaskReq addTodoTaskReq = AddTodoTaskReq.builder().taskId(taskId).taskTitle("待审批文件")
                .userId(apply.getCurrentApproverId()).userNameCn(apply.getCurrentApprover()).detailsUrl(url + "#/approval?id=" + apply.getId())
                .appName("待审批文件").applicantUserId(apply.getApplicantId())
                .applicantUserNameCn(apply.getApplicant())
                .isMsg(1).isShowApplicantUserName(true).applicantId(taskId).build();

        openAPI.addTodoTaskV3(addTodoTaskReq);

    }
}
