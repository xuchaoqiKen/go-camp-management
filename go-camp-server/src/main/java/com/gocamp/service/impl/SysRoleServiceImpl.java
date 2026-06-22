package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.SysRole;
import com.gocamp.entity.SysRolePermission;
import com.gocamp.entity.SysUserRole;
import com.gocamp.mapper.SysRoleMapper;
import com.gocamp.mapper.SysRolePermissionMapper;
import com.gocamp.mapper.SysUserRoleMapper;
import com.gocamp.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Page<SysRole> queryPage(Integer page, Integer size, String roleName, String roleKey, Integer status) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .like(StringUtils.hasText(roleKey), SysRole::getRoleCode, roleKey)
                .eq(status != null, SysRole::getStatus, status)
                .orderByDesc(SysRole::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void createRole(SysRole role) {
        Long count = lambdaQuery().eq(SysRole::getRoleCode, role.getRoleCode()).count();
        if (count > 0) {
            throw new BusinessException("角色标识已存在");
        }
        role.setStatus(1);
        save(role);
    }

    @Override
    @Transactional
    public void updateRole(SysRole role) {
        SysRole existing = getById(role.getId());
        if (existing == null) {
            throw new BusinessException("角色不存在");
        }
        Long count = lambdaQuery().eq(SysRole::getRoleCode, role.getRoleCode())
                .ne(SysRole::getId, role.getId()).count();
        if (count > 0) {
            throw new BusinessException("角色标识已存在");
        }
        existing.setRoleName(role.getRoleName());
        existing.setRoleCode(role.getRoleCode());
        existing.setDescription(role.getDescription());
        existing.setStatus(role.getStatus());
        updateById(existing);
    }

    @Override
    public void toggleStatus(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        role.setStatus(role.getStatus() == 1 ? 0 : 1);
        updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        Long userCount = sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
        if (userCount > 0) {
            throw new BusinessException("该角色已绑定用户，无法删除");
        }
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
        removeById(id);
    }
}