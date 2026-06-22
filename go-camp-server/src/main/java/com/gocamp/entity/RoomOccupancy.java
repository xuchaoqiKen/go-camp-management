package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("room_occupancy")
public class RoomOccupancy {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomId;
    private Long studentId;
    private Long orderId;
    private Long sessionId;
    @TableField("occupancy_source")
    private String occupancySource;
    private String bedNo;
    @TableField("check_in_date")
    private LocalDate checkinDate;
    @TableField("check_out_date")
    private LocalDate checkoutDate;
    @TableField("match_basis")
    private String matchBasis;
    private String status;
    @TableField("release_reason")
    private String releaseReason;
    @TableField("release_operator")
    private String releaseOperator;
    @TableField("release_time")
    private LocalDateTime releaseTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}