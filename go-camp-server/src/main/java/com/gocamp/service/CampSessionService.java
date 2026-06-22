package com.gocamp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gocamp.entity.CampSession;

public interface CampSessionService extends IService<CampSession> {

    void createSession(CampSession session);

    void updateSession(CampSession session);

    void toggleStatus(Long id);
}