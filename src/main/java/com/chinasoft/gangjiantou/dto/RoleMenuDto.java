package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuDto {
    Long roleId;
    String roleName;
    String note;
    List<Long> menuIdList;
}
