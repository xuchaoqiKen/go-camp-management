package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.LeaveRequest;
import com.gocamp.entity.StudentParent;
import com.gocamp.mapper.LeaveRequestMapper;
import com.gocamp.mapper.StudentParentMapper;
import com.gocamp.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveService {

    private final StudentParentMapper studentParentMapper;

    public LeaveServiceImpl(StudentParentMapper studentParentMapper) {
        this.studentParentMapper = studentParentMapper;
    }

    @Override
    @Transactional
    public LeaveRequest apply(LeaveRequest request) {
        // Check 12-hour rule
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = request.getLeaveDate().atTime(8, 0); // default 8am
        long hoursUntil = ChronoUnit.HOURS.between(now, targetTime);
        if (hoursUntil < 12) {
            throw new BusinessException("已超过12小时时间红线，无法提交请假申请");
        }

        // Check duplicate
        long count = count(new LambdaQueryWrapper<LeaveRequest>()
                .eq(LeaveRequest::getStudentId, request.getStudentId())
                .eq(LeaveRequest::getLeaveDate, request.getLeaveDate())
                .eq(LeaveRequest::getLeaveType, request.getLeaveType()));
        if (count > 0) {
            throw new BusinessException("该日期该类型已存在请假申请，不可重复提交");
        }

        request.setStatus("PENDING");
        request.setSource("PARENT");
        request.setCreateTime(LocalDateTime.now());
        save(request);
        return request;
    }

    @Override
    public Page<LeaveRequest> listByParent(Long parentUserId, int page, int size) {
        List<StudentParent> links = studentParentMapper.selectList(
                new LambdaQueryWrapper<StudentParent>().eq(StudentParent::getParentUserId, parentUserId));
        if (links.isEmpty()) return new Page<>();

        List<Long> studentIds = links.stream().map(StudentParent::getStudentId).toList();
        return page(new Page<>(page, size),
                new LambdaQueryWrapper<LeaveRequest>().in(LeaveRequest::getStudentId, studentIds)
                        .orderByDesc(LeaveRequest::getCreateTime));
    }

    @Override
    public Page<LeaveRequest> listByAdmin(String keyword, Long sessionId, String status, int page, int size) {
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        if (sessionId != null) wrapper.eq(LeaveRequest::getSessionId, sessionId);
        if (status != null && !status.isEmpty()) wrapper.eq(LeaveRequest::getStatus, status);
        wrapper.orderByDesc(LeaveRequest::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void approve(Long id, String status, String opinion, Long approverId) {
        LeaveRequest request = getById(id);
        if (request == null) throw new BusinessException("请假申请不存在");
        if (!"PENDING".equals(request.getStatus())) throw new BusinessException("该申请已处理");

        request.setStatus(status);
        request.setReviewerId(approverId);
        request.setReviewOpinion(opinion);
        request.setReviewTime(LocalDateTime.now());
        updateById(request);
    }

    @Override
    @Transactional
    public LeaveRequest manualAdd(LeaveRequest request, Long operatorId) {
        request.setSource("ADMIN");
        request.setStatus("APPROVED");
        request.setReviewerId(operatorId);
        request.setReviewTime(LocalDateTime.now());
        request.setCreateTime(LocalDateTime.now());
        save(request);
        return request;
    }
}