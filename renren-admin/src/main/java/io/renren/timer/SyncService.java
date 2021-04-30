package io.renren.timer;


import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.*;

import io.renren.modules.sys.dao.SysDeptDao;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.dto.SysDeptDTO;
import io.renren.modules.sys.entity.SysDeptEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    SysDeptDao sysDeptDao;

    @Autowired
    SysUserDao sysUserDao;

    public void syncDepts() {
        log.info("=====begin sync depts=======");
        TenantInfoRes tenantInfoRes = openAPI.getTenantInfo();
        DeptDetailRes rootDept = new DeptDetailRes();
        rootDept.setDeptCode("0");
        rootDept.setFatherCode("-1");
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
        SysDeptEntity sysDeptEntity = new SysDeptEntity();
        sysDeptEntity.setId(Long.valueOf(deptDetailRes.getDeptCode()));
        sysDeptEntity.setName(deptDetailRes.getDeptNameCn());
        sysDeptEntity.setPid(Long.valueOf(deptDetailRes.getFatherCode()));
        sysDeptEntity.setSort(deptDetailRes.getOrderNo() == null ? 1 : Integer.valueOf(deptDetailRes.getOrderNo()));
        sysDeptEntity.setCreateDate(new Date());
        sysDeptEntity.setTenantCode(1001L);
        sysDeptDao.insert(sysDeptEntity);
    }

    public void delDepts() {
        log.info("=====begin del depts=======");
        List<Long> list = new ArrayList<>();
        sysDeptDao.selectList(null).stream().forEach(e -> list.add(e.getId()));
        if (!list.isEmpty())
            sysDeptDao.deleteBatchIds(list);
        log.info("=====end del depts=======");
    }

    //    public List<SyncDeptResItem> editDepts(List<DeptInfo> deptList) {
//        log.info("=====begin edit depts=======");
//        List<SyncDeptResItem> resultList = new ArrayList<>();
//        try {
//            int fromIndex = 0;
//            int toIndex;
//            do {
//                toIndex = fromIndex + 100;
//                if (toIndex > deptList.size())
//                    toIndex = deptList.size();
//                List<DeptInfo> list = deptList.subList(fromIndex, toIndex);
//                SyncDeptReq syncDeptReq = new SyncDeptReq();
//                syncDeptReq.setDeptInfo(list);
//                SyncDepartmentsRes syncDepartmentsRes = openAPI.updateDepartments(authRes.getAccess_token(), syncDeptReq);
//                fromIndex = toIndex;
//            } while (toIndex < deptList.size());
//
//            fromIndex = 0;
//
//            try {
//                Thread.sleep(2000);//等待1s，服务器更新数据
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            do {
//                toIndex = fromIndex + 100;
//                if (toIndex > deptList.size())
//                    toIndex = deptList.size();
//                List<DeptInfo> list = deptList.subList(fromIndex, toIndex);
//                SyncDeptReq syncDeptReq = new SyncDeptReq();
//                syncDeptReq.setDeptInfo(list);
//                SyncDepartmentsRes syncDepartmentsRes = openAPI.syncDepartmentStatus(authRes.getAccess_token(), syncDeptReq);
//                fromIndex = toIndex;
//                resultList.addAll(syncDepartmentsRes.getData());
//            } while (toIndex < deptList.size());
//        sendOfficialAccountMsg("编辑部门信息", "编辑成功啦~~~~~");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new SyncException("同步修改部门到WeLink", SyncStatus.DataSynchronizationException, e.getMessage());
//        }
//        log.info("=====end edit depts=======");
//        return resultList;
//    }
//
    public void syncUsers() {
        log.info("=====begin sync users=======");

        List<SysDeptEntity> list = sysDeptDao.selectList(null);
        for (SysDeptEntity sysDeptEntity : list) {
            int pageNum = 1;
            while (true) {
                QueryUserInfoResPage queryUserInfoResPage = openAPI.getUsersByDeptCode(String.valueOf(sysDeptEntity.getId()), pageNum, 50);
                for (UserBasicInfoRes userBasicInfoRes : queryUserInfoResPage.getData()) {
                    SysUserEntity sysUserEntity = new SysUserEntity();
                    sysUserEntity.setHeadUrl(userBasicInfoRes.getAvatar());
                    sysUserEntity.setUsername(userBasicInfoRes.getUserId());
                    sysUserEntity.setRealName(userBasicInfoRes.getUserNameCn());
                    sysUserEntity.setEmail(userBasicInfoRes.getUserEmail());
                    sysUserEntity.setMobile(userBasicInfoRes.getMobileNumber());
                    sysUserEntity.setDeptId(sysDeptEntity.getId());
                    sysUserEntity.setTenantCode(1001L);

                    SysUserEntity entity = sysUserDao.getByUsername(userBasicInfoRes.getUserId());
                    if (entity == null)
                        sysUserDao.insert(sysUserEntity);
                    else {
                        sysUserEntity.setId(entity.getId());
                        sysUserDao.updateById(sysUserEntity);
                    }
                }
                if (queryUserInfoResPage.getHasMore() == 1)//最后一页
                    break;
                else pageNum++;
            }
        }
        log.info("=====end sync users=======");
    }

//    public List<Object> delUsers(List<PersonInfo> personInfoList) {
//        log.info("=====begin del users=======");
//        List<Object> resultList = new ArrayList<>();
//        try {
//            for (PersonInfo personInfo : personInfoList) {
//                DelUserReq delUserReq = new DelUserReq();
//                delUserReq.setCorpUserId(personInfo.getCorpUserId());
//                Object result = openAPI.delUser(authRes.getAccess_token(), delUserReq);
//                resultList.add(result);
//            }
//            sendOfficialAccountMsg("删除人员信息", "删除成功啦~~~~~");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new SyncException("同步删除用户到WeLink", SyncStatus.DataSynchronizationException, e.getMessage());
//        }
//        log.info("=====end del users=======");
//        return resultList;
//    }
//
//    public List<SyncUserResItem> editUsers(List<PersonInfo> personInfoList) {
//        log.info("=====begin edit users=======");
//        List<SyncUserResItem> resultList = new ArrayList<>();
//        try {
//            int fromIndex = 0;
//            int toIndex;
//            do {
//                toIndex = fromIndex + 100;
//                if (toIndex > personInfoList.size())
//                    toIndex = personInfoList.size();
//                List<PersonInfo> list = personInfoList.subList(fromIndex, toIndex);
//                SyncUsersReq syncUsersReq = new SyncUsersReq();
//                syncUsersReq.setPersonInfo(list);
//                SyncUsersRes syncUsersRes = openAPI.updateUsers(authRes.getAccess_token(), syncUsersReq);
//                fromIndex = toIndex;
//            } while (toIndex < personInfoList.size());
//
//            fromIndex = 0;
//
//            try {
//                Thread.sleep(2000);//等待1s，服务器更新数据
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            do {
//                toIndex = fromIndex + 100;
//                if (toIndex > personInfoList.size())
//                    toIndex = personInfoList.size();
//                List<PersonInfo> list = personInfoList.subList(fromIndex, toIndex);
//                SyncUsersReq syncUsersReq = new SyncUsersReq();
//                syncUsersReq.setPersonInfo(list);
//                SyncUsersRes syncUsersRes = openAPI.syncUserStatus(authRes.getAccess_token(), syncUsersReq);
//                fromIndex = toIndex;
//                resultList.addAll(syncUsersRes.getData());
//            } while (toIndex < personInfoList.size());
//            sendOfficialAccountMsg("修改人员信息", "修改成功啦~~~~~");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new SyncException("同步修改用户到WeLink", SyncStatus.DataSynchronizationException, e.getMessage());
//        }
//        log.info("=====end edit users=======");
//        return resultList;
//    }
//
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
