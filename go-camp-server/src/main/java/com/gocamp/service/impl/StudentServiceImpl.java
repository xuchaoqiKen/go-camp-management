package com.gocamp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.OperationLog;
import com.gocamp.entity.Student;
import com.gocamp.mapper.OperationLogMapper;
import com.gocamp.mapper.StudentMapper;
import com.gocamp.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public Student createStudent(Student student) {
        if (student.getAge() != null && (student.getAge() < 7 || student.getAge() > 15)) {
            throw new BusinessException("学员年龄需在7-15岁之间");
        }
        save(student);
        return student;
    }

    @Override
    public void updateStudent(Student student) {
        Student existing = getById(student.getId());
        if (existing == null) {
            throw new BusinessException("学员不存在");
        }
        updateById(student);
    }

    @Override
    public void updateRank(Long studentId, String rank, String rankCategory, String remark) {
        Student student = getById(studentId);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }
        String oldRank = student.getRankCode();
        String oldCategory = student.getRankCategory();
        student.setRankCode(rank);
        student.setRankCategory(rankCategory);
        updateById(student);

        // Log the change
        OperationLog log = new OperationLog();
        log.setTargetType("STUDENT");
        log.setTargetId(studentId);
        log.setAction("UPDATE_RANK");
        log.setBeforeData(oldRank + "/" + oldCategory);
        log.setAfterData(rank + "/" + rankCategory);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    @Override
    public void updateCampStatus(Long studentId, String campStatus) {
        Student student = getById(studentId);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }
        String oldStatus = student.getCampStatus();
        student.setCampStatus(campStatus);
        updateById(student);

        // Log the change
        OperationLog log = new OperationLog();
        log.setTargetType("STUDENT");
        log.setTargetId(studentId);
        log.setAction("UPDATE_CAMP_STATUS");
        log.setBeforeData(oldStatus);
        log.setAfterData(campStatus);
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
