package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.*;
import com.gocamp.mapper.*;
import com.gocamp.service.RefundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RefundServiceImpl extends ServiceImpl<RefundRequestMapper, RefundRequest> implements RefundService {

    private final StudentParentMapper studentParentMapper;
    private final RegistrationOrderMapper orderMapper;

    public RefundServiceImpl(StudentParentMapper studentParentMapper, RegistrationOrderMapper orderMapper) {
        this.studentParentMapper = studentParentMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public RefundRequest apply(RefundRequest request) {
        // Check 12-hour rule
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = request.getRefundDate().atTime(8, 0);
        long hoursUntil = ChronoUnit.HOURS.between(now, targetTime);
        if (hoursUntil < 12) {
            throw new BusinessException("已超过12小时时间红线，无法提交退款申请");
        }

        // Check order exists and refundable amount
        RegistrationOrder order = orderMapper.selectById(request.getOrderId());
        if (order == null) throw new BusinessException("订单不存在");
        if (request.getRefundAmount().compareTo(order.getTotalAmount()) > 0) {
            throw new BusinessException("退款金额不得超过已支付金额");
        }

        request.setStatus("PENDING");
        request.setSource("PARENT");
        request.setCreateTime(LocalDateTime.now());
        save(request);
        return request;
    }

    @Override
    public Page<RefundRequest> listByParent(Long parentUserId, int page, int size) {
        List<StudentParent> links = studentParentMapper.selectList(
                new LambdaQueryWrapper<StudentParent>().eq(StudentParent::getParentUserId, parentUserId));
        if (links.isEmpty()) return new Page<>();

        List<Long> studentIds = links.stream().map(StudentParent::getStudentId).toList();
        return page(new Page<>(page, size),
                new LambdaQueryWrapper<RefundRequest>().in(RefundRequest::getStudentId, studentIds)
                        .orderByDesc(RefundRequest::getCreateTime));
    }

    @Override
    public Page<RefundRequest> listByAdmin(String keyword, Long sessionId, String status, int page, int size) {
        LambdaQueryWrapper<RefundRequest> wrapper = new LambdaQueryWrapper<>();
        if (sessionId != null) wrapper.eq(RefundRequest::getSessionId, sessionId);
        if (status != null && !status.isEmpty()) wrapper.eq(RefundRequest::getStatus, status);
        wrapper.orderByDesc(RefundRequest::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void approve(Long id, String status, String opinion, Long reviewerId) {
        RefundRequest request = getById(id);
        if (request == null) throw new BusinessException("退款申请不存在");
        if (!"PENDING".equals(request.getStatus())) throw new BusinessException("该申请已处理");

        request.setStatus(status);
        request.setReviewerId(reviewerId);
        request.setReviewOpinion(opinion);
        request.setReviewTime(LocalDateTime.now());
        updateById(request);
    }

    @Override
    @Transactional
    public void executeRefund(Long id, Long operatorId) {
        RefundRequest request = getById(id);
        if (request == null) throw new BusinessException("退款申请不存在");
        if (!"APPROVED".equals(request.getStatus())) throw new BusinessException("退款申请未通过审核");

        // Simulate WeChat refund
        request.setStatus("REFUNDED");
        request.setWxRefundId("WX_REFUND_" + System.currentTimeMillis());
        request.setRefundTime(LocalDateTime.now());
        request.setRetryCount(request.getRetryCount() == null ? 1 : request.getRetryCount() + 1);
        updateById(request);
    }

    @Override
    public RefundRequest getDetail(Long id) {
        return getById(id);
    }
}