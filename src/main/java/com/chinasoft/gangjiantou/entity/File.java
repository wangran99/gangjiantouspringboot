package com.chinasoft.gangjiantou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 上传/修订文件信息表
 * </p>
 *
 * @author WangRan
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 相对路径
     */
    private String path;

    /**
     * 文件uuid
     */
    private String uuid;

    /**
     * 文件名后缀
     */
    private String type;

    /**
     * 编辑后的文件对应的原始上传文件ID
     */
    private Long source;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 申请id
     */
    private Long applyId;

    /**
     * 审批环节ID
     */
    private Long approvalId;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;


}
