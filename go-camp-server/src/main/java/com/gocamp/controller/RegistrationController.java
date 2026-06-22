package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.dto.RegistrationRequest;
import com.gocamp.dto.RegistrationResult;
import com.gocamp.service.RegistrationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/submit")
    public Result<RegistrationResult> submit(@RequestBody RegistrationRequest request) {
        return Result.success(registrationService.submitRegistration(request));
    }

    @GetMapping("/order/{orderId}")
    public Result<?> getOrder(@PathVariable Long orderId) {
        return Result.success(registrationService.getOrderDetail(orderId));
    }

    @GetMapping("/orders/parent")
    public Result<?> listParentOrders(@RequestParam Long parentUserId,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return Result.success(registrationService.getOrdersByParent(parentUserId));
    }
}