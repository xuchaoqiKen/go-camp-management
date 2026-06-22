package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_record")
public class PaymentRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String paymentNo;
    private Long parentUserId;
    private BigDecimal totalAmount;
    private Integer paymentStatus;
    private String wxTransactionId;
    private String wxPayOrderNo;
    private LocalDateTime payTime;
    private String payType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}