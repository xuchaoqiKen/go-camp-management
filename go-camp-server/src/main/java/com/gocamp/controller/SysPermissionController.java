package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.SysPermission;
import com.gocamp.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @GetMapping("/tree")
    public Result<List<SysPermission>> getTree() {
        return Result.success(sysPermissionService.getPermissionTree());
    }

    @GetMapping
    public Result<List<SysPermission>> list() {
        return Result.success(sysPermissionService.list());
    }

    @GetMapping("/{id}")
    public Result<SysPermission> getById(@PathVariable Long id) {
        return Result.success(sysPermissionService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody SysPermission permission) {
        sysPermissionService.createPermission(permission);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysPermission permission) {
        permission.setId(id);
        sysPermissionService.updatePermission(permission);
        return Result.success();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        sysPermissionService.toggleStatus(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysPermissionService.deletePermission(id);
        return Result.success();
    }
}