package com.gocamp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.common.Result;
import com.gocamp.entity.SysRole;
import com.gocamp.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<Page<SysRole>> queryPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleKey,
            @RequestParam(required = false) Integer status) {
        return Result.success(sysRoleService.queryPage(page, size, roleName, roleKey, status));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<java.util.List<SysRole>> list() {
        return Result.success(sysRoleService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Void> create(@RequestBody SysRole role) {
        sysRoleService.createRole(role);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> update(@RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return Result.success();
    }

    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        sysRoleService.toggleStatus(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }
}