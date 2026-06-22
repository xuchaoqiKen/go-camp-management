package com.gocamp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gocamp.entity.DailySchedule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DailyScheduleMapper extends BaseMapper<DailySchedule> {
}