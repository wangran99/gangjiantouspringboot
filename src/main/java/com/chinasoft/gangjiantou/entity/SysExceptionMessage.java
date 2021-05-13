package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 异常信息管理
 * </p>
 *
 * @author WangRan
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysExceptionMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常信息编码
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 异常来源
     */
    private String abnormalSource;

    /**
     * 异常名称
     */
    private String name;

    /**
     * 异常类型
     */
    private String type;

    /**
     * 异常内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
