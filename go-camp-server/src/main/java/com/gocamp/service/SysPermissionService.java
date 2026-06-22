package com.gocamp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gocamp.entity.SysPermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {

    List<SysPermission> getPermissionTree();

    void createPermission(SysPermission permission);

    void updatePermission(SysPermission permission);

    void toggleStatus(Long id);

    void deletePermission(Long id);
}