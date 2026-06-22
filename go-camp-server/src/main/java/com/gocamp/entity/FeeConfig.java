package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fee_config")
public class FeeConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String feeCode;
    private String feeName;
    private String feeType;
    private String chargeUnit;
    private BigDecimal unitPrice;
    private Long sessionId;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private Integer status;
    private String remark;
    private Long operatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}