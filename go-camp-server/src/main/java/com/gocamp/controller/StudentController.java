package com.gocamp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.common.Result;
import com.gocamp.entity.Student;
import com.gocamp.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String name,
                          @RequestParam(required = false) String rank,
                          @RequestParam(required = false) String campStatus,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Student::getName, name);
        }
        if (StringUtils.hasText(rank)) {
            wrapper.eq(Student::getRankCode, rank);
        }
        if (StringUtils.hasText(campStatus)) {
            wrapper.eq(Student::getCampStatus, campStatus);
        }
        return Result.success(studentService.page(new Page<>(page, size), wrapper));
    }

    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }

    @PutMapping("/rank")
    public Result<?> updateRank(@RequestParam Long studentId,
                                @RequestParam String rank,
                                @RequestParam(required = false) String rankCategory,
                                @RequestParam(required = false) String remark) {
        studentService.updateRank(studentId, rank, rankCategory, remark);
        return Result.success();
    }

    @PutMapping("/{id}/camp-status")
    public Result<?> updateCampStatus(@PathVariable Long id, @RequestBody UpdateCampStatusRequest request) {
        studentService.updateCampStatus(id, request.getCampStatus());
        return Result.success();
    }

    @lombok.Data
    public static class UpdateCampStatusRequest {
        private String campStatus;
    }
}
