package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_config_history")
public class FeeConfigHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long feeConfigId;
    private BigDecimal oldUnitPrice;
    private BigDecimal newUnitPrice;
    private String changeReason;
    private Long operatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}