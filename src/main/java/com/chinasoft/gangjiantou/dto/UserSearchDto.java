package com.chinasoft.gangjiantou.dto;

import lombok.Data;

@Data
public class UserSearchDto {
    /**
     * 部门id
     */
    String deptCode;
    /**
     * 岗位id
     */
    Long positionId;
}
