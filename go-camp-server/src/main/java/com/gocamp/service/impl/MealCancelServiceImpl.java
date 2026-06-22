package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.MealCancelRequest;
import com.gocamp.mapper.MealCancelRequestMapper;
import com.gocamp.service.MealCancelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class MealCancelServiceImpl extends ServiceImpl<MealCancelRequestMapper, MealCancelRequest> implements MealCancelService {

    @Override
    @Transactional
    public MealCancelRequest apply(MealCancelRequest request) {
        // Check 2-hour rule
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = request.getCancelDate().atTime(
                "LUNCH".equals(request.getMealType()) ? 12 : 18, 0);
        long hoursUntil = ChronoUnit.HOURS.between(now, targetTime);

        if (hoursUntil < 2) {
            request.setIsOverdue(1);
            request.setStatus("OVERDUE_CONFIRMED");
        } else {
            request.setIsOverdue(0);
            request.setStatus("PENDING");
        }

        request.setCreateTime(LocalDateTime.now());
        save(request);
        return request;
    }

    @Override
    public Page<MealCancelRequest> listByCoach(Long coachUserId, int page, int size) {
        return page(new Page<>(page, size),
                new LambdaQueryWrapper<MealCancelRequest>()
                        .eq(MealCancelRequest::getCoachId, coachUserId)
                        .orderByDesc(MealCancelRequest::getCreateTime));
    }

    @Override
    public Page<MealCancelRequest> listByAdmin(String keyword, Long sessionId, String status, int page, int size) {
        LambdaQueryWrapper<MealCancelRequest> wrapper = new LambdaQueryWrapper<>();
        if (sessionId != null) wrapper.eq(MealCancelRequest::getSessionId, sessionId);
        if (status != null && !status.isEmpty()) wrapper.eq(MealCancelRequest::getStatus, status);
        wrapper.orderByDesc(MealCancelRequest::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void approve(Long id, String status, String opinion, Long approverId) {
        MealCancelRequest request = getById(id);
        if (request == null) throw new BusinessException("撤餐申请不存在");
        if (!"PENDING".equals(request.getStatus())) throw new BusinessException("该申请已处理");

        request.setStatus(status);
        request.setReviewerId(approverId);
        request.setReviewOpinion(opinion);
        request.setReviewTime(LocalDateTime.now());
        updateById(request);
    }
}