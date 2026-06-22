package com.gocamp.controller;

import com.gocamp.common.Result;
import com.gocamp.entity.CampSession;
import com.gocamp.service.CampSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sessions")
@RequiredArgsConstructor
public class CampSessionController {

    private final CampSessionService campSessionService;

    @GetMapping
    public Result<List<CampSession>> list() {
        return Result.success(campSessionService.list());
    }

    @GetMapping("/active")
    public Result<List<CampSession>> listActive() {
        return Result.success(campSessionService.lambdaQuery()
                .eq(CampSession::getStatus, 1)
                .orderByAsc(CampSession::getStartDate)
                .list());
    }

    @GetMapping("/{id}")
    public Result<CampSession> getById(@PathVariable Long id) {
        return Result.success(campSessionService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody CampSession session) {
        campSessionService.createSession(session);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CampSession session) {
        session.setId(id);
        campSessionService.updateSession(session);
        return Result.success();
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        campSessionService.toggleStatus(id);
        return Result.success();
    }
}