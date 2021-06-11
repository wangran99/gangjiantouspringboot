package com.chinasoft.gangjiantou.dto;

import lombok.Data;

@Data
public class SaveDocDto {
    /**
     * 申请对应的id
     */
    Long applyId;
    /**
     * 文档下载链接
     */
    String url;
    /**
     * 当前编辑的文档对应的初始上传文件id
     */
    Long sourceFileId;
}
