package com.chinasoft.gangjiantou.dto;

import lombok.Data;

@Data
public class PositionDto {
    String code;
    String name;
    /**
     * 岗位状态.0:停用。1：正常
     */
    Integer status;
    long pageNum;
    long pageSize;
}
