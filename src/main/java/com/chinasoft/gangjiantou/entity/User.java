package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-13
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
     * 性别
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


}
