package com.gocamp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gocamp.entity.Student;

public interface StudentService extends IService<Student> {

    Student createStudent(Student student);

    void updateStudent(Student student);

    void updateRank(Long studentId, String rank, String rankCategory, String remark);

    void updateCampStatus(Long studentId, String campStatus);
}
