package com.gocamp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.SysDepartment;
import com.gocamp.entity.SysUser;
import com.gocamp.mapper.SysDepartmentMapper;
import com.gocamp.mapper.SysUserMapper;
import com.gocamp.service.SysDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements SysDepartmentService {

    private final SysUserMapper sysUserMapper;

    @Override
    public void updateDepartment(SysDepartment department) {
        SysDepartment existing = getById(department.getId());
        if (existing == null) {
            throw new BusinessException("部门不存在");
        }
        existing.setDeptName(department.getDeptName());
        existing.setDeptCode(department.getDeptCode());
        existing.setLeader(department.getLeader());
        existing.setPhone(department.getPhone());
        existing.setSortOrder(department.getSortOrder());
        existing.setStatus(department.getStatus());
        updateById(existing);
    }

    @Override
    public void toggleStatus(Long id) {
        SysDepartment dept = getById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        if (dept.getStatus() == 1) {
            Long userCount = sysUserMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getDeptId, id)
                            .eq(SysUser::getStatus, 1));
            if (userCount > 0) {
                throw new BusinessException("该部门下存在在职用户，无法停用");
            }
        }
        dept.setStatus(dept.getStatus() == 1 ? 0 : 1);
        updateById(dept);
    }
}