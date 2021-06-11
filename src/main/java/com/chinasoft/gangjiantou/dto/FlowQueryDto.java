package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.util.List;

@Data
public class FlowQueryDto {
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 部门id列表
     */
    private List<String> deptCodeList;

    private Long pageNumber;
    private Long pageSize;
}
