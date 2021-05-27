package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 审批流程定义表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApprovalFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程适用部门id
     */
    private String deptCode;

    /**
     * 适用申请的岗位id
     */
    private Long positionId;

    /**
     * 最大文件数
     */
    private Integer maxFile;

    /**
     * 上传的文件能否编辑：0：不能编辑，1：可以编辑
     */
    private Integer fileEditable;

    /**
     * 流程定义状态：0：不生效，1：生效
     */
    private Integer status;


}
