package com.gocamp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String avatar;
    private Long deptId;
    private Integer status;
    @TableField("open_id")
    private String openid;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
    @TableField(exist = false)
    private String roleName;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private LocalDateTime lastLoginTime;
}
