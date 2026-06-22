package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.CoachSchedule;
import com.gocamp.mapper.CoachScheduleMapper;
import com.gocamp.service.CoachScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoachScheduleServiceImpl extends ServiceImpl<CoachScheduleMapper, CoachSchedule> implements CoachScheduleService {

    @Override
    @Transactional
    public void submitSchedule(List<CoachSchedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            throw new BusinessException("排班数据不能为空");
        }
        for (CoachSchedule schedule : schedules) {
            schedule.setCreateTime(LocalDateTime.now());
            schedule.setUpdateTime(LocalDateTime.now());
            save(schedule);
        }
    }

    @Override
    public List<CoachSchedule> getSchedulesByCoach(Long coachUserId, Long sessionId) {
        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachSchedule::getCoachUserId, coachUserId);
        if (sessionId != null) {
            wrapper.eq(CoachSchedule::getSessionId, sessionId);
        }
        wrapper.orderByAsc(CoachSchedule::getScheduleDate);
        return list(wrapper);
    }

    @Override
    public List<CoachSchedule> getSchedulesByDate(String date, Long sessionId) {
        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachSchedule::getScheduleDate, date);
        if (sessionId != null) {
            wrapper.eq(CoachSchedule::getSessionId, sessionId);
        }
        return list(wrapper);
    }
}