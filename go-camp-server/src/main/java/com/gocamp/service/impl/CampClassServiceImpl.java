package com.gocamp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.entity.CampClass;
import com.gocamp.mapper.CampClassMapper;
import com.gocamp.service.CampClassService;
import org.springframework.stereotype.Service;

@Service
public class CampClassServiceImpl extends ServiceImpl<CampClassMapper, CampClass> implements CampClassService {
}