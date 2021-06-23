package com.chinasoft.gangjiantou.dto;

import com.chinasoft.gangjiantou.entity.CarbonCopy;
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
     * 转移审批人的用户岗位id
     */
    String shiftUserPositionId;

    /**
     * 转移审批人的用户岗位名称
     */
    String shiftUserPositionName;

    /**
     * 抄送用户列表
     */
    List<CarbonCopy> ccList;

}
