package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("camp_class")
public class CampClass {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String className;
    private Long sessionId;
    private String rankDescription;
    private Integer maxStudents;
    private Long headTeacherId;
    private Long coachId;
    private String remark;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}