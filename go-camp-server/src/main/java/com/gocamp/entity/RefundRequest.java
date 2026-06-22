package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("refund_request")
public class RefundRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String refundNo;
    private Long orderId;
    private Long studentId;
    private Long sessionId;
    private Long leaveRequestId;
    private LocalDate refundDate;
    private String refundType;
    private String refundItems;
    private BigDecimal refundAmount;
    private BigDecimal actualRefundAmount;
    private String status;
    private String source;
    private String reason;
    private Long reviewerId;
    private String reviewOpinion;
    private LocalDateTime reviewTime;
    private Long refundExecutorId;
    private LocalDateTime refundTime;
    private String wxRefundId;
    private String refundFailReason;
    private Integer retryCount;
    private LocalDateTime lastRetryTime;
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