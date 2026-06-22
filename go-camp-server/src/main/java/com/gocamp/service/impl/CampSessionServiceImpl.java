package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.CampSession;
import com.gocamp.mapper.CampSessionMapper;
import com.gocamp.service.CampSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CampSessionServiceImpl extends ServiceImpl<CampSessionMapper, CampSession> implements CampSessionService {

    @Override
    public void createSession(CampSession session) {
        if (session.getStartDate() != null && session.getEndDate() != null
                && session.getStartDate().isAfter(session.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }
        // Check date overlap
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<CampSession>()
                .eq(CampSession::getStatus, 1)
                .and(w -> w.le(CampSession::getStartDate, session.getEndDate())
                        .ge(CampSession::getEndDate, session.getStartDate())));
        if (count > 0) {
            throw new BusinessException("营期日期不可重叠");
        }
        session.setStatus(1);
        save(session);
    }

    @Override
    public void updateSession(CampSession session) {
        CampSession existing = getById(session.getId());
        if (existing == null) {
            throw new BusinessException("营期不存在");
        }
        if (session.getStartDate() != null && session.getEndDate() != null
                && session.getStartDate().isAfter(session.getEndDate())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }
        existing.setSessionName(session.getSessionName());
        existing.setStartDate(session.getStartDate());
        existing.setEndDate(session.getEndDate());
        existing.setCheckinDate(session.getCheckinDate());
        existing.setCheckoutDate(session.getCheckoutDate());
        existing.setRemark(session.getRemark());
        updateById(existing);
    }

    @Override
    public void toggleStatus(Long id) {
        CampSession session = getById(id);
        if (session == null) {
            throw new BusinessException("营期不存在");
        }
        session.setStatus(session.getStatus() == 1 ? 0 : 1);
        updateById(session);
    }
}