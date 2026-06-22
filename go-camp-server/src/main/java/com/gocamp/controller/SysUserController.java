package com.gocamp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.common.Result;
import com.gocamp.dto.UserCreateRequest;
import com.gocamp.dto.UserQueryRequest;
import com.gocamp.dto.UserUpdateRequest;
import com.gocamp.entity.SysUser;
import com.gocamp.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:view')")
    public Result<Page<SysUser>> queryPage(UserQueryRequest request) {
        return Result.success(sysUserService.queryPage(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:view')")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Void> create(@Valid @RequestBody UserCreateRequest request) {
        sysUserService.createUser(request);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> update(@Valid @RequestBody UserUpdateRequest request) {
        sysUserService.updateUser(request);
        return Result.success();
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return Result.success();
    }

    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        sysUserService.toggleStatus(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }
}