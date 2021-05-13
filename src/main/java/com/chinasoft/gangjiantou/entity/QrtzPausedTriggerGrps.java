package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author WangRan
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QrtzPausedTriggerGrps implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sched_name", type = IdType.AUTO)
    private String schedName;

    private String triggerGroup;


}
