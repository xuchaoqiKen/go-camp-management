package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String module;
    private String action;
    private String targetType;
    private Long targetId;
    private String beforeData;
    private String afterData;
    private String remark;
    private Long operatorId;
    private String operatorName;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}