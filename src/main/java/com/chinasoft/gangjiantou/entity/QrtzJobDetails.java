package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.sql.Blob;
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
public class QrtzJobDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sched_name", type = IdType.AUTO)
    private String schedName;

    private String jobName;

    private String jobGroup;

    private String description;

    private String jobClassName;

    private String isDurable;

    private String isNonconcurrent;

    private String isUpdateData;

    private String requestsRecovery;

    private Blob jobData;


}
