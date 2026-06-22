package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.RankConfig;
import com.gocamp.service.RankConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ranks")
@RequiredArgsConstructor
public class RankConfigController {

    private final RankConfigService rankConfigService;

    @GetMapping
    public Result<List<RankConfig>> list() {
        return Result.success(rankConfigService.list());
    }

    @GetMapping("/active")
    public Result<List<RankConfig>> listActive() {
        return Result.success(rankConfigService.lambdaQuery()
                .eq(RankConfig::getStatus, 1)
                .orderByAsc(RankConfig::getSortOrder)
                .list());
    }

    @GetMapping("/{id}")
    public Result<RankConfig> getById(@PathVariable Long id) {
        return Result.success(rankConfigService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody RankConfig rankConfig) {
        rankConfigService.createRank(rankConfig);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RankConfig rankConfig) {
        rankConfig.setId(id);
        rankConfigService.updateRank(rankConfig);
        return Result.success();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        rankConfigService.toggleStatus(id);
        return Result.success();
    }
}