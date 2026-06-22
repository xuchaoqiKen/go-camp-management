package com.gocamp.service;

import com.gocamp.entity.CoachSchedule;

import java.util.List;

public interface CoachScheduleService {

    void submitSchedule(List<CoachSchedule> schedules);

    List<CoachSchedule> getSchedulesByCoach(Long coachUserId, Long sessionId);

    List<CoachSchedule> getSchedulesByDate(String date, Long sessionId);
}