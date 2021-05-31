package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 岗位定义表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 岗位编码
     */
    private String positionCode;

    /**
     * 岗位排序
     */
    private Integer orderNum;

    /**
     * 岗位备注
     */
    private String note;

    /**
     * 岗位状态.0:停用。1：正常
     */
    private Integer status;

    /**
     * 岗位是否可编辑/删除.0:不能编辑/删除。1：可编辑/删除
     */
    private Integer editable;

    /**
     * 设置时间
     */
    private LocalDateTime createTime;


}
