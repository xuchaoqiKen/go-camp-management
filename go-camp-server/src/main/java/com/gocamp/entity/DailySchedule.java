package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_schedule")
public class DailySchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long studentId;
    private Long sessionId;
    private LocalDate scheduleDate;
    private Integer hasClass;
    private Integer hasBreakfast;
    private Integer hasLunch;
    private Integer hasDinner;
    private String accommodationType;
    private String breakfastSource;
    private BigDecimal breakfastAmount;
    private Integer breakfastEditable;
    private BigDecimal lunchAmount;
    private BigDecimal dinnerAmount;
    private BigDecimal accommodationAmount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}