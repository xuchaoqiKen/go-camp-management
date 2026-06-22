package com.gocamp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.common.Result;
import com.gocamp.entity.RegistrationOrder;
import com.gocamp.mapper.RegistrationOrderMapper;
import com.gocamp.vo.OrderListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final RegistrationOrderMapper orderMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String studentName,
                          @RequestParam(required = false) String orderNo,
                          @RequestParam(required = false) Long sessionId,
                          @RequestParam(required = false) String paymentStatus,
                          @RequestParam(required = false) String roomMatchStatus,
                          @RequestParam(required = false) String startDate,
                          @RequestParam(required = false) String endDate,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        Page<OrderListVO> orderPage = orderMapper.selectOrderPage(
                new Page<>(page, size),
                studentName,
                orderNo,
                sessionId,
                paymentStatus,
                roomMatchStatus,
                startDate,
                endDate
        );
        return Result.success(orderPage);
    }

    @GetMapping("/{id}")
    public Result<OrderListVO> getDetail(@PathVariable Long id) {
        return Result.success(orderMapper.selectOrderDetail(id));
    }

    @GetMapping("/export")
    public Result<?> export(@RequestParam(required = false) String studentName,
                            @RequestParam(required = false) String orderNo,
                            @RequestParam(required = false) Long sessionId,
                            @RequestParam(required = false) String payStatus) {
        LambdaQueryWrapper<RegistrationOrder> wrapper = new LambdaQueryWrapper<>();
        if (sessionId != null) {
            wrapper.eq(RegistrationOrder::getSessionId, sessionId);
        }
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(RegistrationOrder::getOrderNo, orderNo);
        }
        return Result.success(orderMapper.selectList(wrapper));
    }
}
