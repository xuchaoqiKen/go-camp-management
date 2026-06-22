package com.gocamp.dto;

import lombok.Data;

@Data
public class UserQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String username;
    private String name;
    private String phone;
    private String role;
    private Long departmentId;
    private Integer status;
}