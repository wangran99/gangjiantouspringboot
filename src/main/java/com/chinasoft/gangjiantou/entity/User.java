package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private String userId;

    /**
     * 姓名
     */
    private String userNameCn;

    /**
     * 性别.M:男，F：女，其他：未知
     */
    private String sex;

    /**
     * 手机号
     */
    private String mobileNumber;

    /**
     * 主管部门
     */
    private String mainDeptCode;

    /**
     * 所在部门
     */
    private String deptCode;

    /**
     * 电子邮件
     */
    private String userEmail;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 职位
     */
    private String position;

    /**
     * 是否是企业管理员
     */
    private Integer isAdmin;

    /**
     * 用户状态。1：未开户，2：开户中，3：已开户，4：已销户
     */
    private String status;
    /**
     * 用户角色
     */
    @Transient
    private List<UserRole> roleList;
    /**
     * 用户岗位
     */
    @Transient
    private List<UserPosition> positionList;

}
