package com.gocamp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.entity.RefundRequest;

public interface RefundService {

    RefundRequest apply(RefundRequest request);

    Page<RefundRequest> listByParent(Long parentUserId, int page, int size);

    Page<RefundRequest> listByAdmin(String keyword, Long sessionId, String status, int page, int size);

    void approve(Long id, String status, String opinion, Long approverId);

    void executeRefund(Long id, Long operatorId);

    RefundRequest getDetail(Long id);
}