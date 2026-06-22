package com.gocamp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class UserUpdateRequest {
    @NotNull(message = "用户ID不能为空")
    private Long id;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private Long departmentId;
    private List<Long> roleIds;
}