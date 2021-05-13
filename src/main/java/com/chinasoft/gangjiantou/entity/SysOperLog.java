package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 操作日志记录
 * </p>
 *
 * @author WangRan
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作日志ID
     */
    @TableId(value = "OPER_ID", type = IdType.AUTO)
    private Long operId;

    /**
     * 模块标题
     */
    @TableField("TITLE")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @TableField("BUSINESS_TYPE")
    private String businessType;

    /**
     * 方法名称
     */
    @TableField("METHOD")
    private String method;

    /**
     * 请求方式
     */
    @TableField("REQUEST_METHOD")
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @TableField("OPERATOR_TYPE")
    private String operatorType;

    /**
     * 操作人员
     */
    @TableField("OPER_NAME")
    private String operName;

    /**
     * 部门名称
     */
    @TableField("DEPT_NAME")
    private String deptName;

    /**
     * 请求URL
     */
    @TableField("OPER_URL")
    private String operUrl;

    /**
     * 主机地址
     */
    @TableField("OPER_IP")
    private String operIp;

    /**
     * 操作地点
     */
    @TableField("OPER_LOCATION")
    private String operLocation;

    /**
     * 请求参数
     */
    @TableField("OPER_PARAM")
    private String operParam;

    /**
     * 返回参数
     */
    @TableField("JSON_RESULT")
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    @TableField("ERROR_MSG")
    private String errorMsg;

    /**
     * 操作状态（0正常 1失败）
     */
    private String status;

    /**
     * 操作时间
     */
    @TableField("OPER_TIME")
    private LocalDateTime operTime;


}
