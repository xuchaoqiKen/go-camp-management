package com.gocamp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.common.Result;
import com.gocamp.dto.ApproveRequest;
import com.gocamp.dto.ManualLeaveRequest;
import com.gocamp.entity.LeaveRequest;
import com.gocamp.entity.RefundRequest;
import com.gocamp.mapper.LeaveRequestMapper;
import com.gocamp.mapper.RefundRequestMapper;
import com.gocamp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final LeaveRequestMapper leaveRequestMapper;
    private final RefundRequestMapper refundRequestMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String status,
                          @RequestParam(required = false) String type,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        
        // 标准化状态值
        String normalizedStatus = status;
        if (status != null && !status.isEmpty()) {
            normalizedStatus = status.toLowerCase();
        }
        
        if ("refund".equals(type)) {
            // 退款审批
            LambdaQueryWrapper<RefundRequest> wrapper = new LambdaQueryWrapper<>();
            if (normalizedStatus != null && !normalizedStatus.isEmpty()) {
                wrapper.eq(RefundRequest::getStatus, normalizedStatus);
            }
            wrapper.orderByDesc(RefundRequest::getSubmitTime);
            return Result.success(refundRequestMapper.selectPage(new Page<>(page, size), wrapper));
        } else if ("mealCancel".equals(type)) {
            // 撤餐审批 - 暂返回空列表
            return Result.success(new Page<>());
        } else {
            // 默认请假审批
            LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
            if (normalizedStatus != null && !normalizedStatus.isEmpty()) {
                wrapper.eq(LeaveRequest::getStatus, normalizedStatus);
            }
            wrapper.orderByDesc(LeaveRequest::getSubmitTime);
            return Result.success(leaveRequestMapper.selectPage(new Page<>(page, size), wrapper));
        }
    }

    @PostMapping("/leave")
    public Result<?> approveLeave(@RequestBody ApproveRequest request) {
        LeaveRequest leave = leaveRequestMapper.selectById(request.getId());
        if (leave == null) {
            return Result.error("请假申请不存在");
        }

        Long userId = AuthUtil.getCurrentUserId();
        leave.setReviewerId(userId);
        leave.setReviewTime(LocalDateTime.now());

        if (request.getApproved() != null && !request.getApproved()) {
            leave.setStatus("rejected");
            leave.setReviewOpinion(request.getReason());
        } else {
            leave.setStatus("approved");
        }

        leaveRequestMapper.updateById(leave);
        return Result.success();
    }

    @PostMapping("/refund")
    public Result<?> approveRefund(@RequestBody ApproveRequest request) {
        RefundRequest refund = refundRequestMapper.selectById(request.getId());
        if (refund == null) {
            return Result.error("退款申请不存在");
        }

        Long userId = AuthUtil.getCurrentUserId();
        refund.setReviewerId(userId);
        refund.setReviewTime(LocalDateTime.now());

        if (request.getApproved() != null && !request.getApproved()) {
            refund.setStatus("rejected");
            refund.setReviewOpinion(request.getReason());
        } else {
            refund.setStatus("approved");
        }

        refundRequestMapper.updateById(refund);
        return Result.success();
    }

    @PostMapping("/meal-cancel")
    public Result<?> approveMealCancel(@RequestBody ApproveRequest request) {
        return Result.success();
    }

    @PostMapping("/execute-refund")
    public Result<?> executeRefund(@RequestBody ApproveRequest request) {
        RefundRequest refund = refundRequestMapper.selectById(request.getId());
        if (refund == null) {
            return Result.error("退款申请不存在");
        }

        refund.setStatus("refunded");
        refundRequestMapper.updateById(refund);
        return Result.success();
    }

    @PostMapping("/manual-leave")
    public Result<?> addManualLeave(@RequestBody ManualLeaveRequest request) {
        LeaveRequest leave = new LeaveRequest();
        leave.setRequestNo("LQ" + System.currentTimeMillis());
        leave.setStudentId(request.getStudentId());
        leave.setSessionId(Long.parseLong(request.getSessionId()));
        leave.setLeaveDate(LocalDate.parse(request.getLeaveDate(), DateTimeFormatter.ISO_DATE));
        leave.setLeaveType(request.getType().toLowerCase());
        leave.setReason(request.getReason());
        leave.setSource("manual");
        leave.setStatus("pending");
        leave.setOperator("admin");
        leave.setSubmitTime(LocalDateTime.now());

        leaveRequestMapper.insert(leave);
        return Result.success();
    }

    @GetMapping("/leave/{id}")
    public Result<?> getLeaveDetail(@PathVariable Long id) {
        return Result.success(leaveRequestMapper.selectById(id));
    }
}
