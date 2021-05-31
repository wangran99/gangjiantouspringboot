package com.chinasoft.gangjiantou.controller;


import com.chinasoft.gangjiantou.entity.*;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.*;
import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.AddTodoTaskReq;
import com.github.wangran99.welink.api.client.openapi.model.AddTodoTaskRes;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
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
@RequestMapping("/apply")
public class ApplyController {

    private String url="";
    @Autowired
    IUserService userService;
    @Autowired
    IApplyService applyService;
    @Autowired
    IFlowApproverService flowApproverService;
    @Autowired
    IApprovalFlowService approvalFlowService;
    @Autowired
    IUserPositionService userPositionService;
    @Autowired
    IFileService fileService;
    @Autowired
    ICarbonCopyService carbonCopyService;

    @Autowired
    ITodoTaskService todoTaskService;

    @Autowired
    RedisService redisService;
    @Autowired
    OpenAPI openAPI;

    /**
     * 每一次申请上传文件，需要提前获取文件临时id
     * @return
     */
    @GetMapping("fileTempId")
    Long getFile(){
        ThreadLocalRandom threadRandom = ThreadLocalRandom.current();
        Long randomLong = threadRandom.nextLong(0L,Long.MAX_VALUE);
        return randomLong;
    }

    /**
     * 获取我能发起申请的列表
     * @param authCode
     * @return
     */
    @GetMapping("myFlow")
    List<ApprovalFlow> getApprovalsFlow(@RequestHeader("authCode")String authCode){
        UserBasicInfoRes userBasicInfoRes= redisService.getUserInfo(authCode);
        List<UserPosition> userPositionList = userPositionService.lambdaQuery().eq(UserPosition::getUserId,userBasicInfoRes.getUserId()).list();
       List<ApprovalFlow> approvalFlowList = approvalFlowService.lambdaQuery().in(ApprovalFlow::getDeptCode,userBasicInfoRes.getDeptCodes())
               .in(ApprovalFlow::getPositionId,userPositionList.stream().map(e->e.getPositionId()).collect(Collectors.toList())).list();
       return approvalFlowList;
    }

    /**
     * 新增申请
     * @return
     */
    @PostMapping("add")
    @Transactional
    boolean add(@RequestHeader("authCode") String authCode,@RequestBody Apply apply){
        apply.setCurrentApprover(userService.getById(apply.getCurrentApproverId()).getUserNameCn());
        UserBasicInfoRes userBasicInfoRes= redisService.getUserInfo(authCode);
        apply.setId(null);
        apply.setApplicantId(userBasicInfoRes.getUserId());
        apply.setApplicant(userBasicInfoRes.getUserNameCn());
        LocalDateTime localDateTime=LocalDateTime.now();
        ThreadLocalRandom threadRandom = ThreadLocalRandom.current();
        Long randomLong = threadRandom.nextLong(0L,Long.MAX_VALUE);
        String serialNumber =   String.format("{}{}{}", localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth())
                + randomLong;
        apply.setSerialNumber(serialNumber);

        ApprovalFlow approvalFlow=approvalFlowService.getById(apply.getFlowId());
        List<FlowApprover> flowApproverList =  flowApproverService.lambdaQuery().eq(FlowApprover::getFlowId,approvalFlow.getId()).list();
        apply.setCurrentApproverId(flowApproverList.get(0).getUserId());
        apply.setCurrentApprover(flowApproverList.get(0).getUserName());

        applyService.save(apply);

        fileService.lambdaUpdate().eq(File::getTempId,apply.getFileTempId()).set(File::getApplyId,apply.getId())
                .set(File::getTempId,-1L).update();
        for (String userId:apply.getCcList()){
            CarbonCopy carbonCopy=new CarbonCopy();
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
