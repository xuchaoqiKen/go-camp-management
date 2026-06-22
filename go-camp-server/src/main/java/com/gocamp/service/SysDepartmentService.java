package com.gocamp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gocamp.entity.SysDepartment;

public interface SysDepartmentService extends IService<SysDepartment> {

    void updateDepartment(SysDepartment department);

    void toggleStatus(Long id);
}