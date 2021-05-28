package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.util.List;

@Data
public class BindDto {
    String userId;
    List<Long> positionIdList;
    List<Long> roleIdList;
}
