package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 审批抄送表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CarbonCopy implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请id
     */
    private Long applyId;

    /**
     * 抄送的用户id
     */
    private String userId;

    /**
     * 抄送用户姓名
     */
    private String userName;

    /**
     * 创建者的用户id
     */
    private String createUserId;

    /**
     * 创建者用户姓名
     */
    private String createUserName;

    /**
     * 添加抄送用户的时间
     */
    private LocalDateTime createTime;


}
