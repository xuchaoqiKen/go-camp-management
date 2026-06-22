package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coach_schedule")
public class CoachSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long coachUserId;
    private Long sessionId;
    private LocalDate scheduleDate;
    private Integer hasClass;
    private Integer hasWorkLunch;
    private Integer hasWorkDinner;
    private Integer needAccommodation;
    private BigDecimal lunchCost;
    private BigDecimal dinnerCost;
    private BigDecimal totalCost;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}