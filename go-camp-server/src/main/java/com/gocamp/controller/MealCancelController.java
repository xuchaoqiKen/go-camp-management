package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.MealCancelRequest;
import com.gocamp.service.MealCancelService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal-cancel")
public class MealCancelController {

    private final MealCancelService mealCancelService;

    public MealCancelController(MealCancelService mealCancelService) {
        this.mealCancelService = mealCancelService;
    }

    @PostMapping("/apply")
    public Result<MealCancelRequest> apply(@RequestBody MealCancelRequest request) {
        return Result.success(mealCancelService.apply(request));
    }

    @GetMapping("/coach")
    public Result<?> listByCoach(@RequestParam Long coachUserId,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(mealCancelService.listByCoach(coachUserId, page, size));
    }

    @GetMapping("/admin")
    public Result<?> listByAdmin(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Long sessionId,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(mealCancelService.listByAdmin(keyword, sessionId, status, page, size));
    }

    @PutMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id,
                              @RequestParam String status,
                              @RequestParam(required = false) String opinion,
                              @RequestParam Long approverId) {
        mealCancelService.approve(id, status, opinion, approverId);
        return Result.success();
    }
}