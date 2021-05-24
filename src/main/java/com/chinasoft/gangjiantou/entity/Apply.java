package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 申请表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-24
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
     * 流程定义id
     */
    private Long flowId;

    /**
     * 申请原因说明
     */
    private String note;

    /**
     * 当前审批人id
     */
    private String currentApproverId;

    /**
     * 当前审批人
     */
    private String currentApprover;

    /**
     * 状态（0：审核中 1：已撤回 2：已拒绝 3：审批通过）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime applyTime;

    /**
     * 撤回时间
     */
    private LocalDateTime recallTime;


}
