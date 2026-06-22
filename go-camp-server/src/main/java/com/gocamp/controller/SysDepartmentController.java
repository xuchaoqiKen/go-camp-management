package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.SysDepartment;
import com.gocamp.service.SysDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class SysDepartmentController {

    private final SysDepartmentService sysDepartmentService;

    @GetMapping("/list")
    public Result<List<SysDepartment>> list() {
        return Result.success(sysDepartmentService.list());
    }

    @GetMapping("/{id}")
    public Result<SysDepartment> getById(@PathVariable Long id) {
        return Result.success(sysDepartmentService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysDepartment department) {
        department.setId(id);
        sysDepartmentService.updateDepartment(department);
        return Result.success();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        sysDepartmentService.toggleStatus(id);
        return Result.success();
    }
}