package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class Cipher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 成员单位id
     */
    private String deptId;

    /**
     * 单位名称
     */
    private String deptName;

    /**
     * 密钥
     */
    private String encryptionKey;

    /**
     * 管理人员姓名
     */
    private String manager;

    /**
     * 管理员邮箱
     */
    @TableField("managerEmail")
    private String manageremail;

    /**
     * 父单位id
     */
    private Long pid;


}
