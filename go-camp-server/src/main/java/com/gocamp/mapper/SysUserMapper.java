package com.gocamp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.dto.UserQueryRequest;
import com.gocamp.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    Page<SysUser> queryWithDetail(Page<SysUser> page, @Param("request") UserQueryRequest request);
}
