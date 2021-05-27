package com.chinasoft.gangjiantou.dto;

import com.chinasoft.gangjiantou.entity.Role;
import com.chinasoft.gangjiantou.entity.RoleMenu;
import lombok.Data;

import java.util.List;

@Data
public class RoleMenuDetailDto {
    Role role;
    List<RoleMenu> roleMenuList;
}
