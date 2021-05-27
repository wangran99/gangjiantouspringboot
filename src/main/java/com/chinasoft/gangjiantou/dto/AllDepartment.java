package com.chinasoft.gangjiantou.dto;

import com.chinasoft.gangjiantou.entity.Department;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AllDepartment {
    Department department;
    List<AllDepartment> subDepartment=new ArrayList<>();
}
