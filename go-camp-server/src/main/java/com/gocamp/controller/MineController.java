package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.service.MineService;
import com.gocamp.vo.MineProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "我的", description = "家长端/教练端个人信息")
@RestController
@RequestMapping("/api/mine")
@RequiredArgsConstructor
public class MineController {

    private final MineService mineService;

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<MineProfileVO> getProfile() {
        Long userId = getCurrentUserId();
        return Result.success(mineService.getProfile(userId));
    }

    /**
     * 从Spring Security上下文中获取当前登录用户ID
     * JwtAuthenticationFilter 将 userId (Long) 直接作为 principal 存入
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long userId) {
            return userId;
        }
        throw new com.gocamp.common.BusinessException("未登录或Token已过期");
    }
}
