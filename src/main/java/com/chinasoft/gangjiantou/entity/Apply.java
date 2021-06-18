package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 申请表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Apply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 申请人id
     */
    private String applicantId;

    /**
     * 主题
     */
    private String subject;

    /**
     * 审批单号
     */
    private String serialNumber;

    /**
     * 申请部门名称
     */
    private  String deptName;

    /**
     * 流程定义id
     */
    private Long flowId;

    /**
     * 流程模型名称
     */
    private  String flowName;

    /**
     * 上传的文件能否编辑：0：不能编辑，1：可以编辑
     */
    private  Integer fileEditable;

    /**
     * 申请备注
     */
    private String note;

    /**
     * 申请人岗位
     */
    private String userPositionName;

    /**
     * 当前审批人id
     */
    private String currentApproverId;

    /**
     * 当前审批人
     */
    private String currentApprover;

    /**
     * 整个流程状态（对申请人来讲）（0：待审核 1：已撤回 2：审批中 3：已拒绝 4：审批通过）
     */
    private Integer status;

    /**
     * 转交处理状态（0：未转交 1：待转交审核 2:已转交审批完毕）
     */
    private Integer shiftStatus;

    /**
     * 创建时间
     */
    private LocalDateTime applyTime;

    /**
     * 撤回的时间
     */
    private LocalDateTime recallTime;

    /**
     * 审批完成时间
     */
    private LocalDateTime endTime;

    /**
     * 审批人当前状态：（0：待我审核 1：我已审批通过 2：我已拒绝 3：我已转移审批给别人)
     */
    @TableField(exist = false)
    private  Integer condition;

    /**
     * 申请人上传的原始文件列表
     */
    @TableField(exist = false)
    private  List<File> fileList;

    /**
     * 最新版本的文件列表
     */
    @TableField(exist = false)
    private  List<File> newFileList;

    /**
     * 申请对应的抄送人ID列表
     */
    @TableField(exist = false)
    private  List<String> ccList;
}
