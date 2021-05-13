package com.chinasoft.gangjiantou.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinasoft.gangjiantou.entity.Department;
import com.chinasoft.gangjiantou.entity.User;
import com.chinasoft.gangjiantou.mapper.DepartmentMapper;
import com.chinasoft.gangjiantou.mapper.UserMapper;
import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public void syncDepts() {
        log.info("=====begin sync depts=======");
        TenantInfoRes tenantInfoRes = openAPI.getTenantInfo();
        DeptDetailRes rootDept = new DeptDetailRes();
        rootDept.setDeptCode("0");
        rootDept.setFatherCode("-1");
        rootDept.setHasChildDept(1);
        rootDept.setDeptNameCn(tenantInfoRes.getCompanyNameCn());
        insertDept(rootDept);
        QueryDepartmentInfoResPage queryDepartmentInfoResPage = openAPI.getSubDept("0", 0, 1, 100);
        for (DeptDetailRes deptDetailRes : queryDepartmentInfoResPage.getDepartmentInfo()) {
            insertDept(deptDetailRes);
            if (deptDetailRes.getHasChildDept() == 1) {
                QueryDepartmentInfoResPage subDept = openAPI.getSubDept(deptDetailRes.getDeptCode(), 1, 1, 100);
                for (DeptDetailRes temp : subDept.getDepartmentInfo()) {
                    insertDept(temp);
                }
            }
        }
        log.info("=====end sync depts=======");
    }

    private void insertDept(DeptDetailRes deptDetailRes) {
        Department department = new Department();
        department.setDeptCode(deptDetailRes.getDeptCode());
        department.setDeptNameCn(deptDetailRes.getDeptNameCn());
        department.setParentCode(deptDetailRes.getFatherCode());
        department.setOrderNo(deptDetailRes.getOrderNo());
        department.setManagerId(department.getManagerId());
        department.setHasChildDept(deptDetailRes.getHasChildDept());
        departmentService.save(department);
    }

    public void delDepts() {
        log.info("=====begin del depts=======");
        List<String> list = new ArrayList<>();
        departmentService.query().list().stream().forEach(e -> list.add(e.getDeptCode()));
        if (!list.isEmpty())
            departmentService.removeByIds(list);
        log.info("=====end del depts=======");
    }


    public void syncUsers() {
        log.info("=====begin sync users=======");
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
                    user.setSex(userBasicInfoRes.getSex());
                    user.setMobileNumber(userBasicInfoRes.getMobileNumber());
                    user.setMainDeptCode(userBasicInfoRes.getMainDeptCode());
                    user.setUserEmail(userBasicInfoRes.getUserEmail());
                    user.setPosition(userBasicInfoRes.getPosition());
                    user.setIsAdmin(userBasicInfoRes.getIsAdmin());

                    userService.saveOrUpdate(user);
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
