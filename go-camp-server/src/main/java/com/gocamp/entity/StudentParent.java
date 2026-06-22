package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("student_parent")
public class StudentParent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long parentUserId;
    private String relationship;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}