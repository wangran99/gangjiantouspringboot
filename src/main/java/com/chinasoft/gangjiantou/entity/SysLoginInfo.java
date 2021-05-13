package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统访问记录
 * </p>
 *
 * @author WangRan
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysLoginInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "INFO_ID", type = IdType.AUTO)
    private Long infoId;

    /**
     * 登录账号
     */
    @TableField("USER_NAME")
    private String userName;

    /**
     * 登录IP地址
     */
    @TableField("IPADDR")
    private String ipaddr;

    /**
     * 登录地点
     */
    @TableField("LOGIN_LOCATION")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @TableField("BROWSER")
    private String browser;

    /**
     * 操作系统
     */
    @TableField("OS")
    private String os;

    /**
     * 提示消息
     */
    @TableField("MSG")
    private String msg;

    /**
     * 登录状态（0正常 1失败）
     */
    private String status;

    /**
     * 访问时间
     */
    @TableField("LOGIN_TIME")
    private LocalDateTime loginTime;


}
