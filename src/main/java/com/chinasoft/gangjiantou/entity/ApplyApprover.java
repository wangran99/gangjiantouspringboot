package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 审批过程经过的审批人(包含转发审批人)表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApplyApprover implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 审批请求id
     */
    private Long applyId;

    /**
     * 审批人id
     */
    private String approverId;

    /**
     * 审批人姓名
     */
    private String approverName;


    /**
     * 审批人岗位
     */
    private String positionName;

    /**
     * 下一个审批环节id
     */
    private Long nextApplyApprover;

    /**
     * 状态（-1：未轮到审核 0：待审核 1：审批通过 2：已拒绝 3：转移审批给别人）
     */
    private Integer status;

    /**
     * 当前审批环节是否是转交后的审批环节（0：不是转交的环节 1：转交的审批环节）
     */
    private Integer shiftFlag;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 文件修改意见
     */
    private String fileComment;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 每一级审批对应修改后的文件列表
     */
    private transient List<File> fileList;


}
