package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.SysPermission;
import com.gocamp.entity.SysRolePermission;
import com.gocamp.mapper.SysPermissionMapper;
import com.gocamp.mapper.SysRolePermissionMapper;
import com.gocamp.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<SysPermission> getPermissionTree() {
        List<SysPermission> all = list(new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSortOrder));
        List<SysPermission> roots = all.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .collect(Collectors.toList());
        for (SysPermission root : roots) {
            root.setChildren(getChildren(root.getId(), all));
        }
        return roots;
    }

    private List<SysPermission> getChildren(Long parentId, List<SysPermission> all) {
        List<SysPermission> children = all.stream()
                .filter(p -> parentId.equals(p.getParentId()))
                .collect(Collectors.toList());
        for (SysPermission child : children) {
            child.setChildren(getChildren(child.getId(), all));
        }
        return children;
    }

    @Override
    public void createPermission(SysPermission permission) {
        Long count = lambdaQuery().eq(SysPermission::getPermCode, permission.getPermCode()).count();
        if (count > 0) {
            throw new BusinessException("权限标识已存在");
        }
        if (permission.getParentId() != null && permission.getParentId() > 0) {
            SysPermission parent = getById(permission.getParentId());
            if (parent == null) {
                throw new BusinessException("父级权限不存在");
            }
        }
        permission.setStatus(1);
        save(permission);
    }

    @Override
    public void updatePermission(SysPermission permission) {
        SysPermission existing = getById(permission.getId());
        if (existing == null) {
            throw new BusinessException("权限不存在");
        }
        Long count = lambdaQuery().eq(SysPermission::getPermCode, permission.getPermCode())
                .ne(SysPermission::getId, permission.getId()).count();
        if (count > 0) {
            throw new BusinessException("权限标识已存在");
        }
        existing.setPermName(permission.getPermName());
        existing.setPermCode(permission.getPermCode());
        existing.setPermType(permission.getPermType());
        existing.setParentId(permission.getParentId());
        existing.setPath(permission.getPath());
        existing.setComponent(permission.getComponent());
        existing.setIcon(permission.getIcon());
        existing.setSortOrder(permission.getSortOrder());
        existing.setStatus(permission.getStatus());
        updateById(existing);
    }

    @Override
    public void toggleStatus(Long id) {
        SysPermission perm = getById(id);
        if (perm == null) {
            throw new BusinessException("权限不存在");
        }
        perm.setStatus(perm.getStatus() == 1 ? 0 : 1);
        updateById(perm);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        SysPermission perm = getById(id);
        if (perm == null) {
            throw new BusinessException("权限不存在");
        }
        Long childCount = lambdaQuery().eq(SysPermission::getParentId, id).count();
        if (childCount > 0) {
            throw new BusinessException("存在子权限，无法删除");
        }
        Long refCount = sysRolePermissionMapper.selectCount(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getPermissionId, id));
        if (refCount > 0) {
            throw new BusinessException("该权限已被角色引用，无法删除");
        }
        removeById(id);
    }
}