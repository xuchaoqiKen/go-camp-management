package com.gocamp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gocamp.entity.SysUser;
import com.gocamp.dto.UserQueryRequest;
import com.gocamp.dto.UserCreateRequest;
import com.gocamp.dto.UserUpdateRequest;

public interface SysUserService extends IService<SysUser> {
    Page<SysUser> queryPage(UserQueryRequest request);
    void createUser(UserCreateRequest request);
    void updateUser(UserUpdateRequest request);
    void resetPassword(Long userId);
    void toggleStatus(Long userId);
    void deleteUser(Long userId);
}