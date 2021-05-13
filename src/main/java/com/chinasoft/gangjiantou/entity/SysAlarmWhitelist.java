package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 告警白名单
 * </p>
 *
 * @author WangRan
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysAlarmWhitelist implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 白名单编码
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


}
