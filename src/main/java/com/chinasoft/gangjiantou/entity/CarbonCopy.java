package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 审批抄送表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-24
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
     * 访客申请id
     */
    private Long applyId;

    /**
     * 收到待办消息的用户id
     */
    private String userId;

    /**
     * 收到待办消息的用户姓名
     */
    private String userName;


}
