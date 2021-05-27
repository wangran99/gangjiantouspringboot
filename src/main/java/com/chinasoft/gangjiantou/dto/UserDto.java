package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    String sex;
    List<String> deptList;
    String name;
    long pageNum;
    long pageSize;
}
