package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("registration_order")
public class RegistrationOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private String paymentNo;
    private Long studentId;
    private Long sessionId;
    private Integer isContinuous;
    private String sessionIds;
    private String rankCategory;
    private String accommodationType;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String orderStatus;
    private String roomMatchStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}