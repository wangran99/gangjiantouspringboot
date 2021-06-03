package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApprovalDto {
    Long applyId;
    /**
     * 审批意见
     */
    String comment;
    /**
     * 文件修改意见
     */
    String fileComment;
    /**
     * 转移审批人的用户id（仅转移审批时用到这个字段）
     */
    String shiftUserId;
    /**
     * 抄送用户id列表
     */
    List<String> ccList;

}
