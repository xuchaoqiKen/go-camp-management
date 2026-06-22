package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.dto.UserCreateRequest;
import com.gocamp.dto.UserQueryRequest;
import com.gocamp.dto.UserUpdateRequest;
import com.gocamp.entity.SysUser;
import com.gocamp.entity.SysUserRole;
import com.gocamp.mapper.SysUserMapper;
import com.gocamp.mapper.SysUserRoleMapper;
import com.gocamp.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<SysUser> queryPage(UserQueryRequest request) {
        return baseMapper.queryWithDetail(new Page<>(request.getPage(), request.getSize()), request);
    }

    @Override
    @Transactional
    public void createUser(UserCreateRequest request) {
        Long count = lambdaQuery().eq(SysUser::getUsername, request.getUsername()).count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        count = lambdaQuery().eq(SysUser::getPhone, request.getPhone()).count();
        if (count > 0) {
            throw new BusinessException("手机号已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setRealName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setDeptId(request.getDepartmentId());
        user.setStatus(1);
        save(user);

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (Long roleId : request.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateRequest request) {
        SysUser user = getById(request.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Long count = lambdaQuery().eq(SysUser::getPhone, request.getPhone())
                .ne(SysUser::getId, request.getId()).count();
        if (count > 0) {
            throw new BusinessException("手机号已存在");
        }

        user.setRealName(request.getName());
        user.setPhone(request.getPhone());
        user.setDeptId(request.getDepartmentId());
        updateById(user);

        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, user.getId());
        sysUserRoleMapper.delete(wrapper);

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (Long roleId : request.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }
    }

    @Override
    public void resetPassword(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        updateById(user);
    }

    @Override
    public void toggleStatus(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        updateById(user);
    }

    @Override
    public void deleteUser(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(wrapper);
        removeById(userId);
    }
}