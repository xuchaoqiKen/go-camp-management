package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.entity.*;
import com.gocamp.mapper.*;
import com.gocamp.service.DispatchService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DispatchServiceImpl implements DispatchService {

    private final DailyScheduleMapper dailyScheduleMapper;
    private final CoachScheduleMapper coachScheduleMapper;
    private final StudentMapper studentMapper;
    private final RegistrationOrderMapper orderMapper;
    private final RoomOccupancyMapper roomOccupancyMapper;
    private final CampSessionMapper campSessionMapper;

    public DispatchServiceImpl(DailyScheduleMapper dailyScheduleMapper, CoachScheduleMapper coachScheduleMapper,
                                StudentMapper studentMapper, RegistrationOrderMapper orderMapper,
                                RoomOccupancyMapper roomOccupancyMapper, CampSessionMapper campSessionMapper) {
        this.dailyScheduleMapper = dailyScheduleMapper;
        this.coachScheduleMapper = coachScheduleMapper;
        this.studentMapper = studentMapper;
        this.orderMapper = orderMapper;
        this.roomOccupancyMapper = roomOccupancyMapper;
        this.campSessionMapper = campSessionMapper;
    }

    @Override
    public Map<String, Object> getDailyStats(Long sessionId, String date) {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 构建查询条件
        LambdaQueryWrapper<DailySchedule> dailyQuery = new LambdaQueryWrapper<>();
        dailyQuery.eq(DailySchedule::getSessionId, sessionId);
        
        LambdaQueryWrapper<CoachSchedule> coachQuery = new LambdaQueryWrapper<>();
        coachQuery.eq(CoachSchedule::getSessionId, sessionId);
        
        LambdaQueryWrapper<RoomOccupancy> roomQuery = new LambdaQueryWrapper<>();
        roomQuery.eq(RoomOccupancy::getSessionId, sessionId);
        
        // 只有当日期参数传入时才添加日期条件
        LocalDate queryDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
        if (queryDate != null) {
            dailyQuery.eq(DailySchedule::getScheduleDate, queryDate);
            coachQuery.eq(CoachSchedule::getScheduleDate, queryDate);
            roomQuery.le(RoomOccupancy::getCheckinDate, queryDate)
                     .ge(RoomOccupancy::getCheckoutDate, queryDate);
        }

        // 学员总数
        dailyQuery.eq(DailySchedule::getHasClass, 1);
        Long studentCount = dailyScheduleMapper.selectCount(dailyQuery);
        stats.put("studentCount", studentCount);

        // 教练总数
        coachQuery.eq(CoachSchedule::getHasClass, 1);
        Long coachCount = coachScheduleMapper.selectCount(coachQuery);
        stats.put("coachCount", coachCount);

        // 重置查询条件并重新构建
        dailyQuery.clear();
        dailyQuery.eq(DailySchedule::getSessionId, sessionId);
        if (queryDate != null) {
            dailyQuery.eq(DailySchedule::getScheduleDate, queryDate);
        }
        
        // 总餐次
        dailyQuery.eq(DailySchedule::getHasBreakfast, 1);
        long breakfastCount = dailyScheduleMapper.selectCount(dailyQuery);
        
        dailyQuery.clear();
        dailyQuery.eq(DailySchedule::getSessionId, sessionId);
        if (queryDate != null) {
            dailyQuery.eq(DailySchedule::getScheduleDate, queryDate);
        }
        dailyQuery.eq(DailySchedule::getHasLunch, 1);
        long lunchCount = dailyScheduleMapper.selectCount(dailyQuery);
        
        dailyQuery.clear();
        dailyQuery.eq(DailySchedule::getSessionId, sessionId);
        if (queryDate != null) {
            dailyQuery.eq(DailySchedule::getScheduleDate, queryDate);
        }
        dailyQuery.eq(DailySchedule::getHasDinner, 1);
        long dinnerCount = dailyScheduleMapper.selectCount(dailyQuery);
        
        // 教练餐
        coachQuery.clear();
        coachQuery.eq(CoachSchedule::getSessionId, sessionId);
        if (queryDate != null) {
            coachQuery.eq(CoachSchedule::getScheduleDate, queryDate);
        }
        coachQuery.eq(CoachSchedule::getHasWorkLunch, 1);
        long coachLunch = coachScheduleMapper.selectCount(coachQuery);
        
        coachQuery.clear();
        coachQuery.eq(CoachSchedule::getSessionId, sessionId);
        if (queryDate != null) {
            coachQuery.eq(CoachSchedule::getScheduleDate, queryDate);
        }
        coachQuery.eq(CoachSchedule::getHasWorkDinner, 1);
        long coachDinner = coachScheduleMapper.selectCount(coachQuery);

        long totalMeals = breakfastCount + lunchCount + dinnerCount + coachLunch + coachDinner;
        stats.put("breakfastCount", breakfastCount);
        stats.put("lunchCount", lunchCount + coachLunch);
        stats.put("dinnerCount", dinnerCount + coachDinner);
        stats.put("totalMeals", totalMeals);

        // 占用床位
        Long occupiedBeds = roomOccupancyMapper.selectCount(roomQuery);
        stats.put("occupiedBeds", occupiedBeds);

        return stats;
    }

    @Override
    public Map<String, Object> getDailyTable(Long sessionId, String startDate, String endDate, int page, int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        // 构建查询条件
        LambdaQueryWrapper<DailySchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DailySchedule::getSessionId, sessionId);
        
        // 只有当日期参数传入时才添加日期条件
        boolean hasDateFilter = false;
        LocalDate start = null;
        LocalDate end = null;
        
        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate);
            hasDateFilter = true;
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate);
            hasDateFilter = true;
        }
        
        if (hasDateFilter) {
            if (start != null && end != null) {
                queryWrapper.between(DailySchedule::getScheduleDate, start, end);
            } else if (start != null) {
                queryWrapper.ge(DailySchedule::getScheduleDate, start);
            } else if (end != null) {
                queryWrapper.le(DailySchedule::getScheduleDate, end);
            }
        }
        
        // 查询日程
        List<DailySchedule> schedules = dailyScheduleMapper.selectList(queryWrapper);
        
        // 按日期分组
        Map<LocalDate, List<DailySchedule>> dateGroupedSchedules = new LinkedHashMap<>();
        for (DailySchedule ds : schedules) {
            dateGroupedSchedules.computeIfAbsent(ds.getScheduleDate(), k -> new ArrayList<>()).add(ds);
        }
        
        // 生成日期列表
        List<LocalDate> allDates = new ArrayList<>();
        if (hasDateFilter && start != null && end != null) {
            // 有完整日期范围，生成范围内所有日期
            LocalDate current = start;
            while (!current.isAfter(end)) {
                allDates.add(current);
                current = current.plusDays(1);
            }
        } else {
            // 没有日期过滤或范围不完整，使用查询到的所有日期
            allDates.addAll(dateGroupedSchedules.keySet());
            // 按日期排序
            Collections.sort(allDates);
        }
        
        // 构建每日统计数据
        List<Map<String, Object>> allRecords = new ArrayList<>();
        for (LocalDate date : allDates) {
            Map<String, Object> dayStats = new LinkedHashMap<>();
            dayStats.put("date", date.toString());
            
            List<DailySchedule> daySchedules = dateGroupedSchedules.getOrDefault(date, new ArrayList<>());
            
            // 学员总数（当天有课的）
            long studentCount = daySchedules.stream()
                    .filter(ds -> ds.getHasClass() != null && ds.getHasClass() == 1)
                    .count();
            dayStats.put("studentCount", studentCount);
            
            // 教练总数
            Long coachCount = coachScheduleMapper.selectCount(
                    new LambdaQueryWrapper<CoachSchedule>()
                            .eq(CoachSchedule::getSessionId, sessionId)
                            .eq(CoachSchedule::getScheduleDate, date)
                            .eq(CoachSchedule::getHasClass, 1)
            );
            dayStats.put("coachCount", coachCount != null ? coachCount : 0L);
            
            // 早餐数（学员）
            long breakfastCount = daySchedules.stream()
                    .filter(ds -> ds.getHasBreakfast() != null && ds.getHasBreakfast() == 1)
                    .count();
            dayStats.put("breakfastCount", breakfastCount);
            
            // 午餐数（学员 + 教练）
            long lunchCount = daySchedules.stream()
                    .filter(ds -> ds.getHasLunch() != null && ds.getHasLunch() == 1)
                    .count();
            Long coachLunch = coachScheduleMapper.selectCount(
                    new LambdaQueryWrapper<CoachSchedule>()
                            .eq(CoachSchedule::getSessionId, sessionId)
                            .eq(CoachSchedule::getScheduleDate, date)
                            .eq(CoachSchedule::getHasWorkLunch, 1)
            );
            dayStats.put("lunchCount", lunchCount + (coachLunch != null ? coachLunch : 0L));
            
            // 晚餐数（学员 + 教练）
            long dinnerCount = daySchedules.stream()
                    .filter(ds -> ds.getHasDinner() != null && ds.getHasDinner() == 1)
                    .count();
            Long coachDinner = coachScheduleMapper.selectCount(
                    new LambdaQueryWrapper<CoachSchedule>()
                            .eq(CoachSchedule::getSessionId, sessionId)
                            .eq(CoachSchedule::getScheduleDate, date)
                            .eq(CoachSchedule::getHasWorkDinner, 1)
            );
            dayStats.put("dinnerCount", dinnerCount + (coachDinner != null ? coachDinner : 0L));
            
            // 总餐次
            dayStats.put("totalMeals", breakfastCount + lunchCount + dinnerCount + 
                    (coachLunch != null ? coachLunch : 0L) + (coachDinner != null ? coachDinner : 0L));
            
            // 占用床位
            Long occupiedBeds = roomOccupancyMapper.selectCount(
                    new LambdaQueryWrapper<RoomOccupancy>()
                            .eq(RoomOccupancy::getSessionId, sessionId)
                            .le(RoomOccupancy::getCheckinDate, date)
                            .ge(RoomOccupancy::getCheckoutDate, date)
            );
            dayStats.put("occupiedBeds", occupiedBeds != null ? occupiedBeds : 0L);
            
            allRecords.add(dayStats);
        }
        
        // 手动分页
        int total = allRecords.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<Map<String, Object>> pageRecords = fromIndex < total ? allRecords.subList(fromIndex, toIndex) : new ArrayList<>();
        
        result.put("records", pageRecords);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public List<Map<String, Object>> getRankDistribution(Long sessionId, String date) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 构建查询条件
        LambdaQueryWrapper<DailySchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DailySchedule::getSessionId, sessionId)
                    .eq(DailySchedule::getHasClass, 1);
        
        // 只有当日期参数传入时才添加日期条件
        if (date != null && !date.isEmpty()) {
            queryWrapper.eq(DailySchedule::getScheduleDate, LocalDate.parse(date));
        }
        
        List<DailySchedule> schedules = dailyScheduleMapper.selectList(queryWrapper);
        
        // 按日期分组
        Map<LocalDate, List<DailySchedule>> dateGrouped = new LinkedHashMap<>();
        for (DailySchedule ds : schedules) {
            dateGrouped.computeIfAbsent(ds.getScheduleDate(), k -> new ArrayList<>()).add(ds);
        }
        
        // 获取所有日期并排序
        List<LocalDate> allDates = new ArrayList<>(dateGrouped.keySet());
        Collections.sort(allDates);
        
        // 对每一天统计段位分布
        for (LocalDate d : allDates) {
            Map<String, Object> dayData = new LinkedHashMap<>();
            dayData.put("date", d.getMonthValue() + "月" + d.getDayOfMonth() + "日");
            
            Map<String, Long> distribution = new LinkedHashMap<>();
            distribution.put("noneBase", 0L);
            distribution.put("level10_18", 0L);
            distribution.put("dan1", 0L);
            distribution.put("dan2", 0L);
            distribution.put("dan3", 0L);
            distribution.put("dan4", 0L);
            distribution.put("dan5", 0L);
            distribution.put("dan6Plus", 0L);

            List<DailySchedule> daySchedules = dateGrouped.get(d);
            for (DailySchedule ds : daySchedules) {
                Student student = studentMapper.selectById(ds.getStudentId());
                if (student != null) {
                    String rankCode = student.getRankCode();
                    if (rankCode == null) {
                        continue;
                    }
                    // 将段位编码映射到字段
                    switch (rankCode) {
                        case "无基础":
                            distribution.merge("noneBase", 1L, Long::sum);
                            break;
                        case "10-18级":
                        case "10-1级":
                            distribution.merge("level10_18", 1L, Long::sum);
                            break;
                        case "1段":
                            distribution.merge("dan1", 1L, Long::sum);
                            break;
                        case "2段":
                            distribution.merge("dan2", 1L, Long::sum);
                            break;
                        case "3段":
                            distribution.merge("dan3", 1L, Long::sum);
                            break;
                        case "4段":
                            distribution.merge("dan4", 1L, Long::sum);
                            break;
                        case "5段":
                            distribution.merge("dan5", 1L, Long::sum);
                            break;
                        case "6段+":
                            distribution.merge("dan6Plus", 1L, Long::sum);
                            break;
                        default:
                            break;
                    }
                }
            }
            
            dayData.putAll(distribution);
            result.add(dayData);
        }
        
        return result;
    }
}
