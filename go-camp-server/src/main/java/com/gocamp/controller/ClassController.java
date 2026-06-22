package com.gocamp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gocamp.common.Result;
import com.gocamp.entity.*;
import com.gocamp.mapper.RegistrationOrderMapper;
import com.gocamp.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClassController {

    private final CampClassService classService;
    private final ClassStudentService classStudentService;
    private final StudentService studentService;
    private final CampSessionService sessionService;
    private final SysUserService userService;
    private final RankConfigService rankConfigService;
    private final RegistrationOrderMapper orderMapper;

    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> list(@RequestParam(required = false) String name,
                                                   @RequestParam(required = false) Long sessionId,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<CampClass> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(CampClass::getClassName, name);
        }
        if (sessionId != null) {
            wrapper.eq(CampClass::getSessionId, sessionId);
        }
        if (status != null) {
            wrapper.eq(CampClass::getStatus, status);
        }
        wrapper.orderByDesc(CampClass::getCreateTime);

        Page<CampClass> classPage = classService.page(new Page<>(page, size), wrapper);

        List<CampSession> sessions = sessionService.list();
        Map<Long, String> sessionMap = sessions.stream().collect(Collectors.toMap(CampSession::getId, CampSession::getSessionName));

        List<SysUser> users = userService.list();
        Map<Long, String> userMap = users.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));

        List<Map<String, Object>> records = classPage.getRecords().stream().map(clazz -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", clazz.getId());
            map.put("name", clazz.getClassName());
            map.put("sessionId", clazz.getSessionId());
            map.put("sessionName", sessionMap.getOrDefault(clazz.getSessionId(), ""));
            map.put("rankDesc", clazz.getRankDescription());
            map.put("maxStudents", clazz.getMaxStudents());
            map.put("advisorId", clazz.getHeadTeacherId());
            map.put("advisorName", userMap.getOrDefault(clazz.getHeadTeacherId(), "未设置"));
            map.put("coachId", clazz.getCoachId());
            map.put("coachName", userMap.getOrDefault(clazz.getCoachId(), "未设置"));
            map.put("remark", clazz.getRemark());
            map.put("status", clazz.getStatus());
            map.put("studentCount", classStudentService.count(
                new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getClassId, clazz.getId())
            ));
            return map;
        }).collect(Collectors.toList());

        Page<Map<String, Object>> result = new Page<>(classPage.getCurrent(), classPage.getSize(), classPage.getTotal());
        result.setRecords(records);

        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        CampClass clazz = classService.getById(id);
        if (clazz == null) {
            return Result.error("班级不存在");
        }

        List<SysUser> users = userService.list();
        Map<Long, String> userMap = users.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));

        Map<String, Object> map = new HashMap<>();
        map.put("id", clazz.getId());
        map.put("name", clazz.getClassName());
        map.put("sessionId", clazz.getSessionId());
        map.put("rankDesc", clazz.getRankDescription());
        map.put("maxStudents", clazz.getMaxStudents());
        map.put("advisorId", clazz.getHeadTeacherId());
        map.put("advisorName", userMap.getOrDefault(clazz.getHeadTeacherId(), "未设置"));
        map.put("coachId", clazz.getCoachId());
        map.put("coachName", userMap.getOrDefault(clazz.getCoachId(), "未设置"));
        map.put("remark", clazz.getRemark());
        map.put("status", clazz.getStatus());

        return Result.success(map);
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> add(@RequestBody ClassForm form) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            return Result.error("班级名称不能为空");
        }
        if (form.getSessionId() == null) {
            return Result.error("所属期数不能为空");
        }

        LambdaQueryWrapper<CampClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CampClass::getClassName, form.getName())
               .eq(CampClass::getSessionId, form.getSessionId());
        if (classService.count(wrapper) > 0) {
            return Result.error("该期数下班级名称已存在");
        }

        CampSession session = sessionService.getById(form.getSessionId());
        if (session == null || session.getStatus() == 0) {
            return Result.error("所属期数不存在或已停用");
        }

        CampClass clazz = new CampClass();
        clazz.setClassName(form.getName());
        clazz.setSessionId(form.getSessionId());
        clazz.setRankDescription(form.getRankDesc());
        clazz.setMaxStudents(form.getMaxStudents());
        clazz.setHeadTeacherId(form.getAdvisorId());
        clazz.setCoachId(form.getCoachId());
        clazz.setRemark(form.getRemark());
        clazz.setStatus(form.getStatus() == null ? 1 : form.getStatus());

        classService.save(clazz);
        return Result.success();
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(@RequestBody ClassForm form) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            return Result.error("班级名称不能为空");
        }
        if (form.getSessionId() == null) {
            return Result.error("所属期数不能为空");
        }

        LambdaQueryWrapper<CampClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CampClass::getClassName, form.getName())
               .eq(CampClass::getSessionId, form.getSessionId())
               .ne(CampClass::getId, form.getId());
        if (classService.count(wrapper) > 0) {
            return Result.error("该期数下班级名称已存在");
        }

        CampClass clazz = classService.getById(form.getId());
        if (clazz == null) {
            return Result.error("班级不存在");
        }

        clazz.setClassName(form.getName());
        clazz.setSessionId(form.getSessionId());
        clazz.setRankDescription(form.getRankDesc());
        clazz.setMaxStudents(form.getMaxStudents());
        clazz.setHeadTeacherId(form.getAdvisorId());
        clazz.setCoachId(form.getCoachId());
        clazz.setRemark(form.getRemark());
        clazz.setStatus(form.getStatus());

        classService.updateById(clazz);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        CampClass clazz = classService.getById(id);
        if (clazz == null) {
            return Result.error("班级不存在");
        }

        long studentCount = classStudentService.count(
            new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getClassId, id)
        );
        if (studentCount > 0) {
            return Result.error("该班级尚有学生，无法删除");
        }

        classService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}/students")
    public Result<Map<String, Object>> getStudents(@PathVariable Long id) {
        CampClass clazz = classService.getById(id);
        if (clazz == null) {
            return Result.error("班级不存在");
        }

        // 获取班级内学生
        List<ClassStudent> relations = classStudentService.list(
            new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getClassId, id)
        );
        List<Long> studentIds = relations.stream().map(ClassStudent::getStudentId).collect(Collectors.toList());
        List<Student> classStudents = studentIds.isEmpty() ? List.of() : studentService.listByIds(studentIds);

        Map<String, String> rankMap = rankConfigService.list().stream()
            .collect(Collectors.toMap(RankConfig::getRankCode, RankConfig::getRankName));

        List<Map<String, Object>> studentVOs = classStudents.stream().map(student -> {
            Map<String, Object> vo = new HashMap<>();
            vo.put("id", student.getId());
            vo.put("name", student.getName());
            vo.put("rankCode", student.getRankCode());
            vo.put("rankName", rankMap.getOrDefault(student.getRankCode(), "未设置"));
            vo.put("age", student.getAge());
            return vo;
        }).collect(Collectors.toList());

        // 获取可选学生（同一期数、已报名确认、未取消/未退营、且不在任何班级中）
        List<RegistrationOrder> confirmedOrders = orderMapper.selectList(new LambdaQueryWrapper<RegistrationOrder>()
            .eq(RegistrationOrder::getSessionId, clazz.getSessionId())
            .eq(RegistrationOrder::getOrderStatus, "confirmed"));

        List<Long> confirmedStudentIds = confirmedOrders.stream()
            .map(RegistrationOrder::getStudentId)
            .distinct()
            .collect(Collectors.toList());

        // 获取当期所有已在班级中的学生
        List<ClassStudent> allRelations = classStudentService.list(
            new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getSessionId, clazz.getSessionId())
        );
        Set<Long> classedStudentIds = allRelations.stream()
            .map(ClassStudent::getStudentId)
            .collect(Collectors.toSet());

        // 可选学生 = 当期已确认报名学生 - 已在班级中学生
        List<Long> availableStudentIds = confirmedStudentIds.stream()
            .filter(sid -> !classedStudentIds.contains(sid))
            .collect(Collectors.toList());

        List<Student> availableStudents = availableStudentIds.isEmpty() ? List.of() : studentService.listByIds(availableStudentIds);

        List<Map<String, Object>> availableVOs = availableStudents.stream().map(student -> {
            Map<String, Object> vo = new HashMap<>();
            vo.put("id", student.getId());
            vo.put("name", student.getName());
            vo.put("rankCode", student.getRankCode());
            vo.put("rankName", rankMap.getOrDefault(student.getRankCode(), "未设置"));
            vo.put("age", student.getAge());
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("students", studentVOs);
        result.put("available", availableVOs);

        return Result.success(result);
    }

    @PostMapping("/add-student")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addStudent(@RequestBody AddStudentRequest request) {
        CampClass clazz = classService.getById(request.getClassId());
        if (clazz == null) {
            return Result.error("班级不存在");
        }

        if (clazz.getStatus() == 0) {
            return Result.error("班级已停用，不能添加学生");
        }

        Student student = studentService.getById(request.getStudentId());
        if (student == null) {
            return Result.error("学生不存在");
        }

        // 校验：同一学生同一期数只能存在一个当前有效班级
        LambdaQueryWrapper<ClassStudent> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(ClassStudent::getStudentId, request.getStudentId())
                    .eq(ClassStudent::getSessionId, clazz.getSessionId());
        if (classStudentService.count(checkWrapper) > 0) {
            return Result.error("该学生在当期数已在其他班级中");
        }

        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(request.getClassId());
        classStudent.setStudentId(request.getStudentId());
        classStudent.setSessionId(clazz.getSessionId());
        classStudentService.save(classStudent);

        return Result.success();
    }

    @PostMapping("/remove-student")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> removeStudent(@RequestBody AddStudentRequest request) {
        LambdaQueryWrapper<ClassStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassStudent::getClassId, request.getClassId())
               .eq(ClassStudent::getStudentId, request.getStudentId());
        classStudentService.remove(wrapper);

        return Result.success();
    }

    @GetMapping("/{id}/export")
    public void exportStudents(@PathVariable Long id, HttpServletResponse response) throws Exception {
        CampClass clazz = classService.getById(id);
        if (clazz == null) {
            response.setContentType("text/plain;charset=utf-8");
            response.getWriter().write("班级不存在");
            return;
        }

        // 获取班级内学生
        List<ClassStudent> relations = classStudentService.list(
            new LambdaQueryWrapper<ClassStudent>().eq(ClassStudent::getClassId, id)
        );
        List<Long> studentIds = relations.stream().map(ClassStudent::getStudentId).collect(Collectors.toList());
        List<Student> students = studentIds.isEmpty() ? List.of() : studentService.listByIds(studentIds);

        Map<String, String> rankMap = rankConfigService.list().stream()
            .collect(Collectors.toMap(RankConfig::getRankCode, RankConfig::getRankName));

        List<SysUser> users = userService.list();
        Map<Long, String> userMap = users.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));

        CampSession session = sessionService.getById(clazz.getSessionId());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + 
            new String((clazz.getClassName() + "学员名单.csv").getBytes("gbk"), "iso-8859-1"));

        StringBuilder sb = new StringBuilder();
        sb.append("序号,姓名,年龄,段位,班主任,授课教练,营期\n");
        
        int index = 1;
        for (Student student : students) {
            sb.append(index++).append(",");
            sb.append(student.getName()).append(",");
            sb.append(student.getAge() == null ? "" : student.getAge()).append(",");
            sb.append(rankMap.getOrDefault(student.getRankCode(), "")).append(",");
            sb.append(userMap.getOrDefault(clazz.getHeadTeacherId(), "")).append(",");
            sb.append(userMap.getOrDefault(clazz.getCoachId(), "")).append(",");
            sb.append(session == null ? "" : session.getSessionName()).append("\n");
        }

        response.getWriter().write(sb.toString());
    }

    @Data
    public static class ClassForm {
        private Long id;
        private String name;
        private Long sessionId;
        private String rankDesc;
        private Integer maxStudents;
        private Long advisorId;
        private Long coachId;
        private String remark;
        private Integer status;
    }

    @Data
    public static class AddStudentRequest {
        private Long classId;
        private Long studentId;
    }
}