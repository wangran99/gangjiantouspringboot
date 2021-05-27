package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.util.List;

@Data
public class Callback {
    Integer status;
    Integer forcesavetype;
    String url;
    String key;
    List<Object> actions;
}
