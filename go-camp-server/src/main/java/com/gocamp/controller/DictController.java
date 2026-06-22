package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.FeeConfig;
import com.gocamp.entity.RankConfig;
import com.gocamp.service.FeeConfigService;
import com.gocamp.service.RankConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class DictController {

    private final FeeConfigService feeConfigService;
    private final RankConfigService rankConfigService;

    @GetMapping("/fee-types")
    public Result<List<FeeConfig>> getFeeTypes() {
        return Result.success(feeConfigService.lambdaQuery()
                .eq(FeeConfig::getStatus, 1)
                .orderByAsc(FeeConfig::getId)
                .list());
    }

    @GetMapping("/fee-types/{id}")
    public Result<FeeConfig> getFeeTypeById(@PathVariable Long id) {
        return Result.success(feeConfigService.getById(id));
    }

    @GetMapping("/rank-options")
    public Result<List<RankConfig>> getRankOptions() {
        return Result.success(rankConfigService.lambdaQuery()
                .eq(RankConfig::getStatus, 1)
                .orderByAsc(RankConfig::getId)
                .list());
    }
}