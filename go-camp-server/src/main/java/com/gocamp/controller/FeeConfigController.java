package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.FeeConfig;
import com.gocamp.service.FeeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fees")
@RequiredArgsConstructor
public class FeeConfigController {

    private final FeeConfigService feeConfigService;

    @GetMapping
    public Result<List<FeeConfig>> list() {
        return Result.success(feeConfigService.list());
    }

    @GetMapping("/active")
    public Result<List<FeeConfig>> listActive() {
        return Result.success(feeConfigService.lambdaQuery()
                .eq(FeeConfig::getStatus, 1)
                .orderByAsc(FeeConfig::getId)
                .list());
    }

    @GetMapping("/{id}")
    public Result<FeeConfig> getById(@PathVariable Long id) {
        return Result.success(feeConfigService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody FeeConfig feeConfig) {
        feeConfigService.createFee(feeConfig);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FeeConfig feeConfig) {
        feeConfig.setId(id);
        feeConfigService.updateFee(feeConfig);
        return Result.success();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        feeConfigService.toggleStatus(id);
        return Result.success();
    }
}