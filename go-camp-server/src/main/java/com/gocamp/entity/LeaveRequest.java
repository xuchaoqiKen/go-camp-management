package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("leave_request")
public class LeaveRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String requestNo;
    private Long studentId;
    private Long sessionId;
    private LocalDate leaveDate;
    private String leaveType;
    private String source;
    private String reason;
    private String status;
    private BigDecimal refundAmount;
    private Long reviewerId;
    private String reviewOpinion;
    private LocalDateTime reviewTime;
    private Integer isOverdue;
    private LocalDateTime submitTime;
    private String operator;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}