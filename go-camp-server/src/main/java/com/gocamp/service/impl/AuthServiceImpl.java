package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gocamp.common.BusinessException;
import com.gocamp.dto.LoginRequest;
import com.gocamp.dto.LoginResponse;
import com.gocamp.dto.WxLoginRequest;
import com.gocamp.entity.SysUser;
import com.gocamp.mapper.SysUserMapper;
import com.gocamp.security.JwtUtil;
import com.gocamp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername())
        );
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被停用，请联系管理员");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername(), null);
        String refreshToken = jwtUtil.generateToken(user.getId(), user.getUsername(), null);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(7200L)
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .realName(user.getRealName())
                        .phone(user.getPhone())
                        .roles(Collections.emptyList())
                        .permissions(Collections.emptyList())
                        .build())
                .build();
    }

    @Override
    public LoginResponse wxLogin(WxLoginRequest request) {
        // TODO: 对接微信小程序登录，通过code换取openid
        throw new BusinessException("微信登录功能开发中");
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException("刷新令牌无效或已过期");
        }
        Long userId = jwtUtil.getUserId(refreshToken);
        String username = jwtUtil.parseToken(refreshToken).getSubject();

        String newAccessToken = jwtUtil.generateToken(userId, username, null);
        String newRefreshToken = jwtUtil.generateToken(userId, username, null);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(7200L)
                .build();
    }

    @Override
    public void logout(String token) {
        // JWT无状态，登出由前端清除token即可
        // 如需黑名单机制可在此实现
    }
}