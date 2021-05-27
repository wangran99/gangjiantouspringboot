package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户岗位表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 岗位ID
     */
    private Long positionId;

    /**
     * 设置时间
     */
    private LocalDateTime createTime;


}
