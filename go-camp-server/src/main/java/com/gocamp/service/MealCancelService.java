package com.gocamp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.entity.MealCancelRequest;

public interface MealCancelService {

    MealCancelRequest apply(MealCancelRequest request);

    Page<MealCancelRequest> listByCoach(Long coachUserId, int page, int size);

    Page<MealCancelRequest> listByAdmin(String keyword, Long sessionId, String status, int page, int size);

    void approve(Long id, String status, String opinion, Long approverId);
}