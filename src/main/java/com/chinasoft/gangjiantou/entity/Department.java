package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门id
     */
    @TableId(value = "dept_code", type = IdType.INPUT)
    private String deptCode;

    /**
     * 父部门id
     */
    private String parentCode;

    /**
     * 部门名称
     */
    private String deptNameCn;

    /**
     * 部门排序
     */
    private Integer orderNo;

    /**
     * 部门层次
     */
    private Integer deptLevel;

    /**
     * 部门管理人员列表
     */
    private String managerId;

    /**
     * 是否有子部门
     */
    private Integer hasChildDept;


}
