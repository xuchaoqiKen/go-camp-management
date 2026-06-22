package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.RefundRequest;
import com.gocamp.service.RefundService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refund")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/apply")
    public Result<RefundRequest> apply(@RequestBody RefundRequest request) {
        return Result.success(refundService.apply(request));
    }

    @GetMapping("/parent")
    public Result<?> listByParent(@RequestParam Long parentUserId,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(refundService.listByParent(parentUserId, page, size));
    }

    @GetMapping("/admin")
    public Result<?> listByAdmin(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Long sessionId,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(refundService.listByAdmin(keyword, sessionId, status, page, size));
    }

    @PutMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id,
                              @RequestParam String status,
                              @RequestParam(required = false) String opinion,
                              @RequestParam Long approverId) {
        refundService.approve(id, status, opinion, approverId);
        return Result.success();
    }

    @PutMapping("/{id}/execute")
    public Result<?> executeRefund(@PathVariable Long id, @RequestParam Long operatorId) {
        refundService.executeRefund(id, operatorId);
        return Result.success();
    }
}