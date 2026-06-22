package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("meal_cancel_request")
public class MealCancelRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String requestNo;
    private Long coachId;
    private Long sessionId;
    private LocalDate cancelDate;
    private String mealType;
    private LocalDateTime submitTime;
    private Integer isOverdue;
    private Integer overdueConfirmed;
    private String status;
    private String reviewOpinion;
    private Long reviewerId;
    private LocalDateTime reviewTime;
    private String operator;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}