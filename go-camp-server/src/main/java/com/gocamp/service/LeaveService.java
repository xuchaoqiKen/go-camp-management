package com.gocamp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.entity.LeaveRequest;

public interface LeaveService {

    LeaveRequest apply(LeaveRequest request);

    Page<LeaveRequest> listByParent(Long parentUserId, int page, int size);

    Page<LeaveRequest> listByAdmin(String keyword, Long sessionId, String status, int page, int size);

    void approve(Long id, String status, String opinion, Long approverId);

    LeaveRequest manualAdd(LeaveRequest request, Long operatorId);
}