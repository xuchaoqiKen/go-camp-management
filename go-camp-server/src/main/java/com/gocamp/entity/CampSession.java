package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("camp_session")
public class CampSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String sessionName;
    @TableField(exist = false)
    private Integer sessionOrder;
    private LocalDate startDate;
    private LocalDate endDate;
    @TableField(exist = false)
    private LocalDate checkinDate;
    @TableField(exist = false)
    private LocalDate checkoutDate;
    private Integer status;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}