package com.gocamp.service;

import com.gocamp.dto.LoginRequest;
import com.gocamp.dto.LoginResponse;
import com.gocamp.dto.WxLoginRequest;

public interface AuthService {

    /**
     * 账号密码登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 微信授权登录
     */
    LoginResponse wxLogin(WxLoginRequest request);

    /**
     * 刷新Token
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 登出
     */
    void logout(String token);
}