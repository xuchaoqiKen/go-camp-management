package com.gocamp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gocamp.entity.SysRole;

public interface SysRoleService extends IService<SysRole> {

    Page<SysRole> queryPage(Integer page, Integer size, String roleName, String roleKey, Integer status);

    void createRole(SysRole role);

    void updateRole(SysRole role);

    void toggleStatus(Long id);

    void deleteRole(Long id);
}