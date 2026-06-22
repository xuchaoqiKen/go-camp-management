package com.gocamp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxLoginRequest {

    @NotBlank(message = "微信授权code不能为空")
    private String code;

    private String encryptedData;
    private String iv;
}