package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色定义表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色备注
     */
    private String note;

    /**
     * 角色状态.0:不可删除。1：可删除
     */
    private Integer status;

    /**
     * 设置时间
     */
    private LocalDateTime createTime;


}
