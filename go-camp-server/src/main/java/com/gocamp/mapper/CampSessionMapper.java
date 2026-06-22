package com.gocamp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gocamp.entity.CampSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CampSessionMapper extends BaseMapper<CampSession> {
}