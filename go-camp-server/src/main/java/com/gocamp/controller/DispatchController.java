package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.service.DispatchService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/stats")
    public Result<?> stats(@RequestParam Long sessionId, @RequestParam(required = false) String date) {
        return Result.success(dispatchService.getDailyStats(sessionId, date));
    }

    @GetMapping("/daily-stats")
    public Result<?> dailyStats(@RequestParam Long sessionId, @RequestParam String date) {
        return Result.success(dispatchService.getDailyStats(sessionId, date));
    }

    @GetMapping("/daily-table")
    public Result<?> dailyTable(@RequestParam Long sessionId,
                                 @RequestParam(required = false) String startDate,
                                 @RequestParam(required = false) String endDate,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return Result.success(dispatchService.getDailyTable(sessionId, startDate, endDate, page, size));
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam Long sessionId,
                          @RequestParam(required = false) String startDate,
                          @RequestParam(required = false) String endDate,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        return Result.success(dispatchService.getDailyTable(sessionId, startDate, endDate, page, size));
    }

    @GetMapping("/rank-distribution")
    public Result<?> rankDistribution(@RequestParam Long sessionId, @RequestParam(required = false) String date) {
        return Result.success(dispatchService.getRankDistribution(sessionId, date));
    }
}