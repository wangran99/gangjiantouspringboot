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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${approval.url}")
    private String url;


    @Value("${welink.openapi.client-id}")
    private String mobileUrl;
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
     * 获取抄送我的申请流程的列表
     *
     * @param authCode
     * @return
     */
    @GetMapping("myCCFlow")
    List<ApprovalFlow> getMyCCFlow(@RequestHeader("authCode") String authCode) {
        UserBasicInfoRes user = redisService.getUserInfo(authCode);
        List<CarbonCopy> carbonCopyList = carbonCopyService.lambdaQuery().eq(CarbonCopy::getUserId, user.getUserId()).list();
        List<Apply> applyList = applyService.lambdaQuery().in(Apply::getId, carbonCopyList.stream().map(e -> e.getApplyId()).collect(Collectors.toList())).list();
        List<ApprovalFlow> approvalFlowList = approvalFlowService.lambdaQuery().in(ApprovalFlow::getId, applyList.stream().map(a -> a.getFlowId()).collect(Collectors.toList())).list();
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
       if(CollectionUtils.isEmpty(apply.getFileList()))
           throw new CommonException("请上传文件");
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        apply.setId(null);
        apply.setApplicantId(userBasicInfoRes.getUserId());
        apply.setApplicant(userBasicInfoRes.getUserNameCn());
        //生成流水号
        LocalDateTime localDateTime = LocalDateTime.now();
        ThreadLocalRandom threadRandom = ThreadLocalRandom.current();
        Long randomLong = threadRandom.nextLong(10000L, Short.MAX_VALUE);
        String serialNumber = String.format("%d%02d%02d", localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth())
                + randomLong;
        apply.setSerialNumber(serialNumber);

        ApprovalFlow approvalFlow = approvalFlowService.getById(apply.getFlowId());
        List<FlowApprover> flowApproverList = flowApproverService.lambdaQuery().eq(FlowApprover::getFlowId, approvalFlow.getId()).orderByAsc(FlowApprover::getId).list();
        apply.setCurrentApproverId(flowApproverList.get(0).getUserId());
        apply.setUserPositionName(positionService.getById(approvalFlow.getPositionId()).getPositionName());
        apply.setCurrentApprover(flowApproverList.get(0).getUserName());
        apply.setFlowName(approvalFlow.getFlowName());
        apply.setFileEditable(approvalFlow.getFileEditable());
        apply.setDeptName(departmentService.getById(approvalFlow.getDeptCode()).getDeptNameCn());

        applyService.save(apply);
        //插入审批人
        List<ApplyApprover> applyApproverList = new ArrayList<>();
        flowApproverList.forEach(e -> {
            ApplyApprover applyApprover = new ApplyApprover();
            applyApprover.setApplyId(apply.getId());
            applyApprover.setApproverId(e.getUserId());
            applyApprover.setApproverName(e.getUserName());
            Position positionTemp = positionService.getById(e.getPositionId());
            applyApprover.setPositionName(positionTemp != null ? positionTemp.getPositionName() : "");
            applyApproverList.add(applyApprover);
        });
        applyApproverList.get(0).setStatus(0);
        applyApproverService.saveBatch(applyApproverList);

        for (int i = 0; i < applyApproverList.size(); i++) {
            ApplyApprover approver = applyApproverList.get(i);
            approver.setApplyId(apply.getId());
            if ((i + 1) < applyApproverList.size()) {
                ApplyApprover next = applyApproverList.get(i + 1);
                approver.setNextApplyApprover(next.getId());

            }
        }

        applyApproverService.updateBatchById(applyApproverList);

        if (CollectionUtils.isNotEmpty(apply.getFileList()))
            fileService.lambdaUpdate().in(File::getId, apply.getFileList().stream().map(e -> e.getId()).collect(Collectors.toList()))
                    .set(File::getApplyId, apply.getId()).update();

        if (CollectionUtils.isNotEmpty(apply.getCcList())) {
            List<CarbonCopy> carbonCopyList = new ArrayList<>();
            apply.getCcList().forEach(e -> {
                CarbonCopy carbonCopy = new CarbonCopy();
                carbonCopy.setApplyId(apply.getId());
                carbonCopy.setUserId(e.getUserId());
                carbonCopy.setUserName(userService.getById(e.getUserId()).getUserNameCn());
                carbonCopy.setCreateUserId(userBasicInfoRes.getUserId());
                carbonCopy.setCreateUserName(userBasicInfoRes.getUserNameCn());
                carbonCopyList.add(carbonCopy);
            });
            carbonCopyService.saveBatch(carbonCopyList);

            SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
            sendOfficialAccountMsgReq.setMsgContent(apply.getApplicant() + "发起了文件审批");
            sendOfficialAccountMsgReq.setMsgOwner("文件审批者");
            sendOfficialAccountMsgReq.setUrlType("html");
            sendOfficialAccountMsgReq.setUrlPath("h5://" + mobileUrl + "/html/index.html?applyid=" + apply.getId());
//            sendOfficialAccountMsgReq.setDesktopUrlPath(url + "/#/detail?id=" + apply.getId());

            sendOfficialAccountMsgReq.setToUserList(apply.getCcList().stream().map(a -> a.getUserId()).collect(Collectors.toList()));
            sendOfficialAccountMsgReq.setMsgTitle("文件审批");
            sendOfficialAccountMsgReq.setMsgRange("0");
//            sendOfficialAccountMsgReq.setMsgDisplayMode(1);
            openAPI.sendOfficialAccountMsg(sendOfficialAccountMsgReq);
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
        if (apply.getStatus() == 1 && !apply.getApplicantId().equals(userBasicInfoRes.getUserId()))
            throw new CommonException("申请人已撤回审批");
        List<ApplyApprover> applyApproverList = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, apply.getId())
                .orderByAsc(ApplyApprover::getId).list();
        List<CarbonCopy> carbonCopyList = carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, id).list();
        List<String> aviableUserIdList = new ArrayList<>();
        aviableUserIdList.add(apply.getApplicantId());
        aviableUserIdList.addAll(applyApproverList.stream().map(e -> e.getApproverId()).collect(Collectors.toList()));
        aviableUserIdList.addAll(carbonCopyList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        if (!aviableUserIdList.contains(userBasicInfoRes.getUserId()))
            throw new CommonException("你无权查看别人的审批流程");
        //获取抄送人员列表
        apply.setCcList(carbonCopyList);
        apply.setFileList(fileService.lambdaQuery().eq(File::getApplyId, id).eq(File::getSource, -1).list());
        //获取最新编辑的文档列表
        List<File> newFileList = new ArrayList<>();
        apply.getFileList().forEach(e -> {
            File file = fileService.lambdaQuery().eq(File::getSource, e.getId()).orderByDesc(File::getUploadTime).last("limit 1").one();
            if (file == null) {
                newFileList.add(e);
            } else {
                newFileList.add(file);
            }
        });
        apply.setNewFileList(newFileList);

        ApplyDto applyDto = new ApplyDto();
        applyDto.setApply(apply);
        applyDto.setApplyApproverList(applyApproverList);
        applyDto.getApplyApproverList().forEach(e -> {
//            if (e.getStatus() != 0)
            e.setFileList(fileService.lambdaQuery()
                    .eq(File::getApprovalId, e.getId()).list());
        });
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
        if (apply.getEndTime() != null)
            throw new CommonException("当前审批已经结束");
        ApplyApprover applyApprover = getActualApprove(apply);
        if (!user.getUserId().equals(applyApprover.getApproverId()))
            throw new CommonException("当前审批人未审核结束,请等待");
        if (applyApprover.getShiftFlag() == 1) {
            apply.setShiftStatus(2);
            apply.setStatus(0);
            applyService.updateById(apply);
        } else {
            apply.setShiftStatus(0);
            apply.setStatus(2);
            applyService.updateById(apply);
        }
        applyApprover.setStatus(1);
        applyApprover.setComment(approvalDto.getComment());
        applyApprover.setFileComment(approvalDto.getFileComment());
        applyApprover.setApprovalTime(LocalDateTime.now());
        applyApproverService.updateById(applyApprover);
        List<CarbonCopy> carbonCopyList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(approvalDto.getCcList())) {
            approvalDto.getCcList().forEach(e -> {
                if (carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, apply.getId()).eq(CarbonCopy::getUserId, e.getUserId()).one() != null)
                    return;
                CarbonCopy carbonCopy = new CarbonCopy();
                carbonCopy.setApplyId(apply.getId());
                carbonCopy.setUserId(e.getUserId());
                carbonCopy.setUserName(userService.getById(e.getUserId()).getUserNameCn());
                carbonCopy.setCreateUserId(user.getUserId());
                carbonCopy.setCreateUserName(user.getUserNameCn());
                carbonCopyList.add(carbonCopy);
            });
            carbonCopyService.saveBatch(carbonCopyList);
        }
        delTodoTaskByApplyId(apply.getId());
        Long nextApplyApproverId = applyApprover.getNextApplyApprover();
        if (nextApplyApproverId == null) {//审批结束
            apply.setStatus(4);
            apply.setShiftStatus(0);
            apply.setEndTime(LocalDateTime.now());
            applyService.updateById(apply);

            SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
            sendOfficialAccountMsgReq.setMsgContent(apply.getApplicant() + "的文件审批通过");
            sendOfficialAccountMsgReq.setMsgOwner("文件审批者");
            sendOfficialAccountMsgReq.setUrlType("html");
            sendOfficialAccountMsgReq.setUrlPath("h5://" + mobileUrl + "/html/index.html?applyid=" + apply.getId());
//            sendOfficialAccountMsgReq.setDesktopUrlPath(url + "/#/detail?id=" + apply.getId());

            List<String> list = new ArrayList<>();
            list.add(apply.getApplicantId());
            list.addAll(carbonCopyList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
            sendOfficialAccountMsgReq.setToUserList(list);
            sendOfficialAccountMsgReq.setMsgTitle("文件审批");
            sendOfficialAccountMsgReq.setMsgRange("0");
//            sendOfficialAccountMsgReq.setMsgDisplayMode(1);
            openAPI.sendOfficialAccountMsg(sendOfficialAccountMsgReq);
        } else {
            ApplyApprover nextApplyApprover = applyApproverService.getById(nextApplyApproverId);
            apply.setCurrentApproverId(nextApplyApprover.getApproverId());
            apply.setCurrentApprover(nextApplyApprover.getApproverName());
            applyService.updateById(apply);
            nextApplyApprover.setStatus(0);
            applyApproverService.updateById(nextApplyApprover);
            addTodoTask(apply);
            SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
            sendOfficialAccountMsgReq.setMsgContent(apply.getApplicant() + "的文件审批待" + apply.getCurrentApprover() + "审批");
            sendOfficialAccountMsgReq.setMsgOwner("文件审批者");
            sendOfficialAccountMsgReq.setUrlType("html");
            sendOfficialAccountMsgReq.setUrlPath("h5://" + mobileUrl + "/html/index.html?applyid=" + apply.getId());
//            sendOfficialAccountMsgReq.setDesktopUrlPath(url + "/#/detail?id=" + apply.getId());

            List<String> list = new ArrayList<>();
            list.add(apply.getApplicantId());
            list.addAll(carbonCopyList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
            sendOfficialAccountMsgReq.setToUserList(list);
            sendOfficialAccountMsgReq.setMsgTitle("文件审批");
            sendOfficialAccountMsgReq.setMsgRange("0");
//            sendOfficialAccountMsgReq.setMsgDisplayMode(1);
            openAPI.sendOfficialAccountMsg(sendOfficialAccountMsgReq);
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
        if (apply.getEndTime() != null)
            throw new CommonException("当前审批已经结束");
        ApplyApprover applyApprover = getActualApprove(apply);
        if (!user.getUserId().equals(applyApprover.getApproverId()))
            throw new CommonException("当前审批人未审核结束,请等待");

        apply.setStatus(3);
        apply.setEndTime(LocalDateTime.now());
        applyService.updateById(apply);

        List<CarbonCopy> carbonCopyList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(approvalDto.getCcList())) {
            approvalDto.getCcList().forEach(e -> {
                if (carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, apply.getId()).eq(CarbonCopy::getUserId, e.getUserId()).one() != null)
                    return;
                CarbonCopy carbonCopy = new CarbonCopy();
                carbonCopy.setApplyId(apply.getId());
                carbonCopy.setUserId(e.getUserId());
                carbonCopy.setUserName(userService.getById(e.getUserId()).getUserNameCn());
                carbonCopy.setCreateUserId(user.getUserId());
                carbonCopy.setCreateUserName(user.getUserNameCn());
                carbonCopyList.add(carbonCopy);
            });
            carbonCopyService.saveBatch(carbonCopyList);
        }

        applyApprover.setStatus(2);
        applyApprover.setComment(approvalDto.getComment());
        applyApprover.setFileComment(approvalDto.getFileComment());
        applyApprover.setApprovalTime(LocalDateTime.now());
        applyApproverService.updateById(applyApprover);
        delTodoTaskByApplyId(apply.getId());
        if(carbonCopyList.size()<1)
            return true;
        SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
        sendOfficialAccountMsgReq.setMsgContent(apply.getApplicant() + "的文件审核被驳回,点击查看详情");
        sendOfficialAccountMsgReq.setMsgOwner("文件审批者");
        sendOfficialAccountMsgReq.setUrlType("html");
//        sendOfficialAccountMsgReq.setUrlPath(url + "/#/detail?id=" + apply.getId());
        sendOfficialAccountMsgReq.setUrlPath("h5://" + mobileUrl + "/html/index.html?applyid=" + apply.getId());
        List<String> list = new ArrayList<>();
//        list.add(apply.getApplicantId());
        list.addAll(carbonCopyList.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        sendOfficialAccountMsgReq.setToUserList(list);
        sendOfficialAccountMsgReq.setMsgTitle("文件审批");
        sendOfficialAccountMsgReq.setMsgRange("0");
//        sendOfficialAccountMsgReq.setMsgDisplayMode(1);
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
        if (apply.getEndTime() != null)
            throw new CommonException("当前审批已经结束");
        ApplyApprover applyApprover = getActualApprove(apply);
        if (!user.getUserId().equals(applyApprover.getApproverId()))
            throw new CommonException("当前审批人未审核结束,请等待");
        if (!user.getUserId().equals(apply.getCurrentApproverId()))
            throw new CommonException("当前审批人未审核结束,请等待");
        apply.setShiftStatus(1);
        applyService.updateById(apply);

        User shiftUser = userService.getById(approvalDto.getShiftUserId());
        applyApprover.setStatus(3);
        applyApprover.setComment(approvalDto.getComment());
        applyApprover.setFileComment(approvalDto.getFileComment());
        applyApprover.setApprovalTime(LocalDateTime.now());
        applyApproverService.updateById(applyApprover);

        //插入被转交环节
        ApplyApprover newApplyApprover = new ApplyApprover();
        newApplyApprover.setApplyId(approvalDto.getApplyId());
        newApplyApprover.setApproverId(shiftUser.getUserId());
        newApplyApprover.setPositionName(approvalDto.getShiftUserPositionName());
        newApplyApprover.setStatus(0);
        newApplyApprover.setShiftFlag(1);
        newApplyApprover.setApproverName(shiftUser.getUserNameCn());
        applyApproverService.save(newApplyApprover);
        applyApprover.setNextApplyApprover(newApplyApprover.getId());
        applyApproverService.updateById(applyApprover);
        //转交后的审批环节先删除、再保存
        List<ApplyApprover> list = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, apply.getId())
                .lt(ApplyApprover::getId, newApplyApprover.getId()).gt(ApplyApprover::getId, applyApprover.getId())
                .orderByAsc(ApplyApprover::getId).list();
        applyApproverService.removeByIds(list.stream().map(e -> e.getId()).collect(Collectors.toList()));
        //创建新的转移审批人环节
        ApplyApprover applyApproverTemp = new ApplyApprover();
        applyApproverTemp.setApplyId(applyApprover.getApplyId());
        applyApproverTemp.setApproverId(applyApprover.getApproverId());
        applyApproverTemp.setPositionName(applyApprover.getPositionName());
        applyApproverTemp.setApproverName(applyApprover.getApproverName());
        list.add(0, applyApproverTemp);
        list.forEach(e -> e.setId(null));
        applyApproverService.saveBatch(list);
        for (int i = 0; i < list.size(); i++) {
            ApplyApprover approver = list.get(i);
            approver.setApplyId(apply.getId());
            if ((i + 1) < list.size()) {
                ApplyApprover next = list.get(i + 1);
                approver.setNextApplyApprover(next.getId());
            }
        }
        applyApproverService.updateBatchById(list);
        newApplyApprover.setNextApplyApprover(list.get(0).getId());
        applyApproverService.updateById(newApplyApprover);

        if (CollectionUtils.isNotEmpty(approvalDto.getCcList())) {
            List<CarbonCopy> carbonCopyList = new ArrayList<>();
            approvalDto.getCcList().forEach(e -> {
                if (carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, apply.getId()).eq(CarbonCopy::getUserId, e.getUserId()).one() != null)
                    return;
                CarbonCopy carbonCopy = new CarbonCopy();
                carbonCopy.setApplyId(apply.getId());
                carbonCopy.setUserId(e.getUserId());
                carbonCopy.setUserName(userService.getById(e.getUserId()).getUserNameCn());
                carbonCopy.setCreateUserId(user.getUserId());
                carbonCopy.setCreateUserName(user.getUserNameCn());
                carbonCopyList.add(carbonCopy);
            });
            carbonCopyService.saveBatch(carbonCopyList);
        }

        addShiftTodoTask(apply);
        SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
        sendOfficialAccountMsgReq.setMsgContent(apply.getApplicant() + "的文件审批转移给" + shiftUser.getUserNameCn() + ",点击查看详情");
        sendOfficialAccountMsgReq.setMsgOwner("文件审批者");
        sendOfficialAccountMsgReq.setUrlType("html");
//        sendOfficialAccountMsgReq.setUrlPath(url + "/#/detail?id=" + apply.getId());
        sendOfficialAccountMsgReq.setUrlPath("h5://" + mobileUrl + "/html/index.html?applyid=" + apply.getId());
        List<String> list1 = new ArrayList<>();
//        list1.add(apply.getApplicantId());
        list1.addAll(carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, apply.getId()).list()
                .stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        sendOfficialAccountMsgReq.setToUserList(list1);
        sendOfficialAccountMsgReq.setMsgTitle("文件审批");
        sendOfficialAccountMsgReq.setMsgRange("0");
//        sendOfficialAccountMsgReq.setMsgDisplayMode(1);
        openAPI.sendOfficialAccountMsg(sendOfficialAccountMsgReq);
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

        SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
        sendOfficialAccountMsgReq.setMsgContent(apply.getApplicant() + "的文件审核被撤回,点击查看详情");
        sendOfficialAccountMsgReq.setMsgOwner("文件审批者");
        sendOfficialAccountMsgReq.setUrlType("html");
//        sendOfficialAccountMsgReq.setUrlPath(url + "/#/detail?id=" + apply.getId());
        sendOfficialAccountMsgReq.setUrlPath("h5://" + mobileUrl + "/html/index.html?applyid=" + apply.getId());
        List<String> list = new ArrayList<>();
        list.add(apply.getApplicantId());
        list.add(apply.getCurrentApproverId());
        list.addAll(carbonCopyService.lambdaQuery().eq(CarbonCopy::getApplyId, apply.getId()).list()
                .stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        sendOfficialAccountMsgReq.setToUserList(list);
        sendOfficialAccountMsgReq.setMsgTitle("文件审批");
        sendOfficialAccountMsgReq.setMsgRange("0");
//        sendOfficialAccountMsgReq.setMsgDisplayMode(1);
        openAPI.sendOfficialAccountMsg(sendOfficialAccountMsgReq);
        return true;
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
//        log.error("dto:"+applyPendingDto.toString());
//        log.error("dto333:"+applyPendingDto.getStatusList().contains("0"));
        Page<Apply> applyPage = applyService.pendingApply(page, userBasicInfoRes.getUserId(), applyPendingDto);
        applyPage.getRecords().forEach(e -> {
            ApplyApprover applyApprover = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, e.getId()).
                    eq(ApplyApprover::getApproverId, userBasicInfoRes.getUserId()).ne(ApplyApprover::getStatus, -1)
                    .orderByDesc(ApplyApprover::getId).last("limit 1").one();
            e.setCondition(applyApprover.getStatus());
        });
        applyPage.getRecords().forEach(e -> {
            e.setApplyApproverList(applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, e.getId())
                    .orderByAsc(ApplyApprover::getId).list());
        });
        return applyPage;
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
        return applyPage;
    }

    /**
     * 分页查询转交我的审批
     *
     * @param authCode
     * @param shiftDto
     * @return
     */
    @PostMapping("queryshift")
    Page<Apply> queryshift(@RequestHeader("authCode") String authCode, @RequestBody ShiftDto shiftDto) {
        UserBasicInfoRes user = redisService.getUserInfo(authCode);
        Page<Apply> page = new Page<>(shiftDto.getPageNum(), shiftDto.getPageSize());
        Page<Apply> applyPage = applyService.queryShift(page, user.getUserId(), shiftDto);
        applyPage.getRecords().forEach(e -> {
            ApplyApprover applyApprover = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, e.getId()).
                    eq(ApplyApprover::getApproverId, user.getUserId()).ne(ApplyApprover::getStatus, -1)
                    .orderByDesc(ApplyApprover::getId).last("limit 1").one();
            e.setCondition(applyApprover.getStatus());
        });
        applyPage.getRecords().forEach(e -> {
            e.setApplyApproverList(applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, e.getId())
                    .orderByAsc(ApplyApprover::getId).list());
        });
        return applyPage;
    }

    private ApplyApprover getActualApprove(Apply apply) {
        if (apply.getEndTime() != null)
            return null;
        List<ApplyApprover> applyApproverList = applyApproverService.lambdaQuery().eq(ApplyApprover::getApplyId, apply.getId())
                .orderByAsc(ApplyApprover::getId).list();
        for (ApplyApprover applyApprover : applyApproverList)
            if (applyApprover.getStatus() == 0)
                return applyApprover;
        return null;
    }

    private void delTodoTaskByApplyId(Long applyId) {
        List<TodoTask> list = todoTaskService.lambdaQuery().eq(TodoTask::getApplyId, applyId).list();
        for (TodoTask todoTask : list)
            openAPI.delTodoTask(todoTask.getTaskId());
        todoTaskService.removeByIds(list.stream().map(e->e.getId()).collect(Collectors.toList()));
    }

    //发送welink审批待办消息提醒
    private void addTodoTask(Apply apply) {
        ApplyApprover applyApprover = getActualApprove(apply);
        TodoTask todoTask = new TodoTask();
        //发送待办任务提醒
        String taskId = UUID.randomUUID().toString().replaceAll("-", "");
        todoTask.setTaskId(taskId);
        todoTask.setApplyId(apply.getId());
        todoTask.setUserId(apply.getCurrentApproverId());
        todoTask.setUserName(apply.getCurrentApprover());
        todoTaskService.save(todoTask);

        AddTodoTaskReq addTodoTaskReq = AddTodoTaskReq.builder().taskId(taskId).taskTitle("待审批文件")
                .userId(applyApprover.getApproverId()).userNameCn(applyApprover.getApproverName())
//                .detailsUrlPc(url + "#/approval?id=" + apply.getId()).detailsUrl("h5://"+mobileUrl+"")
                .detailsUrlPc(url + "#approval?applyid=" + apply.getId())
                .detailsUrl("h5://" + mobileUrl + "/html/index.html?applyid=" + apply.getId())
                .appName("待审批文件").applicantUserId(apply.getApplicantId())
                .applicantUserNameCn(apply.getApplicant())
                .isMsg(1).isShowApplicantUserName(true).applicantId(taskId).build();

        openAPI.addTodoTaskV3(addTodoTaskReq);
    }

    //发送welink审批转移审批待办消息提醒
    private void addShiftTodoTask(Apply apply) {
        ApplyApprover applyApprover = getActualApprove(apply);
        TodoTask todoTask = new TodoTask();
        //发送待办任务提醒
        String taskId = UUID.randomUUID().toString().replaceAll("-", "");
        todoTask.setTaskId(taskId);
        todoTask.setApplyId(apply.getId());
        todoTask.setUserId(apply.getCurrentApproverId());
        todoTask.setUserName(apply.getCurrentApprover());
        todoTaskService.save(todoTask);

        AddTodoTaskReq addTodoTaskReq = AddTodoTaskReq.builder().taskId(taskId).taskTitle(apply.getCurrentApprover() + "转交待审批文件")
                .userId(applyApprover.getApproverId()).userNameCn(applyApprover.getApproverName())
//                .detailsUrlPc(url + "#/approval?id=" + apply.getId()).detailsUrl("h5://"+mobileUrl+"")
                .detailsUrlPc(url + "#approval?applyid=" + apply.getId())
                .detailsUrl("h5://" + mobileUrl + "/html/index.html?applyid=" + apply.getId())
                .appName("待审批文件").applicantUserId(apply.getCurrentApproverId())
                .applicantUserNameCn(apply.getCurrentApprover())
                .isMsg(1).isShowApplicantUserName(true).applicantId(taskId).build();

        openAPI.addTodoTaskV3(addTodoTaskReq);
    }
}
