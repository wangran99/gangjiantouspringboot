package com.chinasoft.gangjiantou.controller;


import com.chinasoft.gangjiantou.dto.AllDepartment;
import com.chinasoft.gangjiantou.entity.Department;
import com.chinasoft.gangjiantou.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-21
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private IDepartmentService departmentService;

    /**
     * 获取所有部门列表
     *
     * @return
     */
    @GetMapping("list")
    public List<Department> list() {
        List<Department> rootList = departmentService.lambdaQuery().ne(Department::getParentCode, 0).list();
        return rootList;
    }

    /**
     * 按结构获取所有部门列表
     *
     * @return
     */
    @GetMapping("all")
    public List<AllDepartment> all() {
        List<Department> rootList = departmentService.lambdaQuery().eq(Department::getParentCode, 0).list();
        List<AllDepartment> allDepartmentList = new ArrayList<>();
        rootList.stream().forEach(department -> {
            AllDepartment allDepartment = new AllDepartment();
            allDepartment.setDepartment(department);
            allDepartmentList.add(allDepartment);
        });
        List<Department> allList = departmentService.list();
        for (AllDepartment allDepartment : allDepartmentList)
            addNode(allDepartment, allList);
        return allDepartmentList;
    }

    private void addNode(AllDepartment allDepartment, List<Department> list) {
        for (Department department : list)
            if (department.getParentCode().equals(allDepartment.getDepartment().getDeptCode())) {
                AllDepartment temp = new AllDepartment();
                temp.setDepartment(department);
                allDepartment.getSubDepartment().add(temp);
                addNode(temp, list);
            }
    }

}
