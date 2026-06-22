package com.gocamp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.entity.ClassStudent;
import com.gocamp.mapper.ClassStudentMapper;
import com.gocamp.service.ClassStudentService;
import org.springframework.stereotype.Service;

@Service
public class ClassStudentServiceImpl extends ServiceImpl<ClassStudentMapper, ClassStudent> implements ClassStudentService {
}