package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 待办消息表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TodoTask implements Serializable {

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
     * 关联的待办事件ID
     */
    private String taskId;

    /**
     * 收到待办消息的用户id
     */
    private String userId;

    /**
     * 收到待办消息的用户姓名
     */
    private String userName;


}
