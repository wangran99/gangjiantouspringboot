package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    /**
     * 性别.M:男，F：女，其他：未知
     */
    String sex;
    /**
     * 部门ID列表
     */
    List<String> deptList;
    /**
     * 姓名
     */
    String name;
    long pageNum;
    long pageSize;
}
