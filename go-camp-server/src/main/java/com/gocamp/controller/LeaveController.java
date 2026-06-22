package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.LeaveRequest;
import com.gocamp.service.LeaveService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/apply")
    public Result<LeaveRequest> apply(@RequestBody LeaveRequest request) {
        return Result.success(leaveService.apply(request));
    }

    @GetMapping("/parent")
    public Result<?> listByParent(@RequestParam Long parentUserId,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(leaveService.listByParent(parentUserId, page, size));
    }

    @GetMapping("/admin")
    public Result<?> listByAdmin(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Long sessionId,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(leaveService.listByAdmin(keyword, sessionId, status, page, size));
    }

    @PutMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id,
                              @RequestParam String status,
                              @RequestParam(required = false) String opinion,
                              @RequestParam Long approverId) {
        leaveService.approve(id, status, opinion, approverId);
        return Result.success();
    }

    @PostMapping("/manual")
    public Result<LeaveRequest> manualAdd(@RequestBody LeaveRequest request, @RequestParam Long operatorId) {
        return Result.success(leaveService.manualAdd(request, operatorId));
    }
}