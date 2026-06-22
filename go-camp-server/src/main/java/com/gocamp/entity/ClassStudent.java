package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("class_student")
public class ClassStudent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long classId;
    private Long studentId;
    private Long sessionId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}