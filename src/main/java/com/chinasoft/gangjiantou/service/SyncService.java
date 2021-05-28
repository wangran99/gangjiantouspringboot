package com.chinasoft.gangjiantou.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinasoft.gangjiantou.entity.*;
import com.chinasoft.gangjiantou.mapper.DepartmentMapper;
import com.chinasoft.gangjiantou.mapper.UserMapper;
import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 同步部门/人员服务
 */
@Service
@Slf4j
public class SyncService {
//    @Value("${welink.sendofficialaccount}")
//    private String sendOfficialAccounts;

    @Autowired
    OpenAPI openAPI;

    @Autowired
    IDepartmentService departmentService;

    @Autowired
    IUserService userService;

    @Autowired
    IUserRoleService userRoleService;

    @Autowired
    IUserPositionService userPositionService;

    public void syncDepts() {
        log.info("=====begin sync depts=======");
        TenantInfoRes tenantInfoRes = openAPI.getTenantInfo();
        List<DeptDetailRes> deptList = new ArrayList<>();
        DeptDetailRes rootDept = new DeptDetailRes();
        rootDept.setDeptCode("0");
        rootDept.setParentCode("-1");
        rootDept.setDeptLevel(0);
        rootDept.setHasChildDept(1);
        rootDept.setDeptNameCn(tenantInfoRes.getCompanyNameCn());
        deptList.add(rootDept);
        QueryDepartmentInfoResPage queryDepartmentInfoResPage = openAPI.getSubDept("0", 0, 1, 100);
        for (DeptDetailRes deptDetailRes : queryDepartmentInfoResPage.getData()) {
            deptList.add(deptDetailRes);
            if (deptDetailRes.getHasChildDept() == 1) {
                QueryDepartmentInfoResPage subDept = openAPI.getSubDept(deptDetailRes.getDeptCode(), 1, 1, 100);
                for (DeptDetailRes temp : subDept.getData()) {
                    deptList.add(temp);
                }
            }
        }
        insertBatchDept(deptList);
        log.info("=====end sync depts=======");
    }

    private void insertBatchDept(List<DeptDetailRes> deptList) {
        List<Department> list = new ArrayList<>();
        for (DeptDetailRes deptDetailRes : deptList) {
            Department department = new Department();
            department.setDeptCode(deptDetailRes.getDeptCode());
            department.setDeptNameCn(deptDetailRes.getDeptNameCn());
            department.setParentCode(deptDetailRes.getParentCode());
            department.setOrderNo(deptDetailRes.getOrderNo());
            department.setDeptLevel(deptDetailRes.getDeptLevel());
            department.setManagerId(deptDetailRes.getManagerId() == null || deptDetailRes.getManagerId().isEmpty() ? null : deptDetailRes.getManagerId().toString());
            department.setHasChildDept(deptDetailRes.getHasChildDept());
            list.add(department);
        }
        departmentService.saveBatch(list);

    }

    public void delDepts() {
        log.info("=====begin del depts=======");
        List<String> list = new ArrayList<>();
        departmentService.query().list().stream().forEach(e -> list.add(e.getDeptCode()));
//        if (!list.isEmpty())
            departmentService.removeByIds(list);
        log.info("=====end del depts=======");
    }


    public void syncUsers() {
        log.info("=====begin sync users=======");
        List<String> userList = new ArrayList<>();
        userService.list().stream().forEach(e -> userList.add(e.getUserId()));
        if (!userList.isEmpty())
            userService.removeByIds(userList);
        List<Department> list = departmentService.list();

        for (Department department : list) {
            int pageNum = 1;
            while (true) {
                QueryUserInfoResPage queryUserInfoResPage = openAPI.getUsersByDeptCode(department.getDeptCode(), pageNum, 50);
                for (UserBasicInfoRes userBasicInfoRes : queryUserInfoResPage.getData()) {
                    User user = new User();
                    user.setUserId(userBasicInfoRes.getUserId());
                    user.setUserNameCn(userBasicInfoRes.getUserNameCn());
                    user.setAvatar(userBasicInfoRes.getAvatar());
                    user.setStatus(userBasicInfoRes.getUserStatus());
                    user.setSex(userBasicInfoRes.getSex());
                    user.setMobileNumber(userBasicInfoRes.getMobileNumber());
                    user.setMainDeptCode(userBasicInfoRes.getMainDeptCode());
                    user.setDeptCode(department.getDeptCode());
                    user.setUserEmail(userBasicInfoRes.getUserEmail());
                    user.setPosition(userBasicInfoRes.getPosition());
                    user.setIsAdmin(userBasicInfoRes.getIsAdmin());

                    User temp = userService.getById(user.getUserId());
                    if (temp == null)
                        userService.save(user);
                    else {
                        String deptCode = temp.getDeptCode();
                        temp.setDeptCode(deptCode + "," + department.getDeptCode());
                        userService.updateById(temp);
                    }
                  List<UserRole> userRoleList=  userRoleService.lambdaQuery().eq(UserRole::getUserId,user.getUserId()).list();
                    if(userRoleList.isEmpty()){
                        UserRole userRole=new UserRole();
                        userRole.setUserId(user.getUserId());
                        userRole.setUserName(user.getUserNameCn());
                        userRole.setRoleId(2L);
                        userRoleService.save(userRole);
                    }
                    List<UserPosition> userPositionList  =userPositionService.lambdaQuery().eq(UserPosition::getUserId,user.getUserId()).list();
                    if(userPositionList.isEmpty()){
                        UserPosition userPosition=new UserPosition();
                        userPosition.setUserId(user.getUserId());
                        userPosition.setUserName(user.getUserNameCn());
                        userPosition.setPositionId(1L);
                        userPositionService.save(userPosition);
                    }
                }
                if (queryUserInfoResPage.getHasMore() == 1)//最后一页
                    break;
                else pageNum++;
            }
        }
        log.info("=====end sync users=======");
    }


//    public void sendOfficialAccountMsg(String title, String content) {
//        String[] accounts = sendOfficialAccounts.split(",");
//        List<String> list = Arrays.asList(accounts);
//        SendOfficialAccountMsgReq sendOfficialAccountMsgReq = new SendOfficialAccountMsgReq();
//        sendOfficialAccountMsgReq.setToUserList(list);
//        sendOfficialAccountMsgReq.setMsgRange("0");
//        sendOfficialAccountMsgReq.setMsgTitle(title);
//        sendOfficialAccountMsgReq.setMsgContent(content);
//        sendOfficialAccountMsgReq.setUrlType("html");
//        sendOfficialAccountMsgReq.setUrlPath("h5://demo.com");
//        sendOfficialAccountMsgReq.setMsgOwner("同步小助手");
//        openAPI.sendOfficialAccountMsg(authRes.getAccess_token(), sendOfficialAccountMsgReq);
//    }
}
