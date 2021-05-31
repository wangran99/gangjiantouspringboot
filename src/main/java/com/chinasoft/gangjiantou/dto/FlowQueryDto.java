package com.chinasoft.gangjiantou.dto;

import lombok.Data;

@Data
public class FlowQueryDto {
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 部门id
     */
    private String deptCode;

    private Long pageNumber;
    private Long pageSize;
}
