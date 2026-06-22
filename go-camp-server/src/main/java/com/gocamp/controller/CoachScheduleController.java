package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.CoachSchedule;
import com.gocamp.service.CoachScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach/schedule")
public class CoachScheduleController {

    private final CoachScheduleService coachScheduleService;

    public CoachScheduleController(CoachScheduleService coachScheduleService) {
        this.coachScheduleService = coachScheduleService;
    }

    @PostMapping("/submit")
    public Result<?> submit(@RequestBody List<CoachSchedule> schedules) {
        coachScheduleService.submitSchedule(schedules);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam Long coachUserId,
                           @RequestParam(required = false) Long sessionId) {
        return Result.success(coachScheduleService.getSchedulesByCoach(coachUserId, sessionId));
    }

    @GetMapping("/by-date")
    public Result<?> listByDate(@RequestParam String date,
                                 @RequestParam Long sessionId) {
        return Result.success(coachScheduleService.getSchedulesByDate(date, sessionId));
    }
}
