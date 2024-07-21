package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);


    //新增员工
    void save(EmployeeDTO employeeDTO);


    //分页查询
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    //员工账号的启用和🈲用
    void startOrStop(Integer status, Long id);

    //根据id查询员工
    Employee getById(Long id);

    //编辑员工
    void update(EmployeeDTO employeeDTO);
}
