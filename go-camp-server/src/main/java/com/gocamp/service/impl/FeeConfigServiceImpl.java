package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.FeeConfig;
import com.gocamp.entity.FeeConfigHistory;
import com.gocamp.mapper.FeeConfigHistoryMapper;
import com.gocamp.mapper.FeeConfigMapper;
import com.gocamp.service.FeeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeeConfigServiceImpl extends ServiceImpl<FeeConfigMapper, FeeConfig> implements FeeConfigService {

    private final FeeConfigHistoryMapper feeConfigHistoryMapper;

    @Override
    public void createFee(FeeConfig feeConfig) {
        // Check code uniqueness
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<FeeConfig>()
                .eq(FeeConfig::getFeeCode, feeConfig.getFeeCode()));
        if (count > 0) {
            throw new BusinessException("费用项编码已存在");
        }
        feeConfig.setStatus(1);
        save(feeConfig);
    }

    @Override
    @Transactional
    public void updateFee(FeeConfig feeConfig) {
        FeeConfig existing = getById(feeConfig.getId());
        if (existing == null) {
            throw new BusinessException("费用项不存在");
        }
        // Record history if price changed
        if (feeConfig.getUnitPrice() != null && !feeConfig.getUnitPrice().equals(existing.getUnitPrice())) {
            FeeConfigHistory history = new FeeConfigHistory();
            history.setFeeConfigId(existing.getId());
            history.setOldUnitPrice(existing.getUnitPrice());
            history.setNewUnitPrice(feeConfig.getUnitPrice());
            history.setChangeReason(feeConfig.getRemark());
            history.setCreateTime(LocalDateTime.now());
            feeConfigHistoryMapper.insert(history);
        }
        existing.setFeeName(feeConfig.getFeeName());
        existing.setUnitPrice(feeConfig.getUnitPrice());
        existing.setChargeUnit(feeConfig.getChargeUnit());
        existing.setSessionId(feeConfig.getSessionId());
        existing.setEffectiveStartDate(feeConfig.getEffectiveStartDate());
        existing.setEffectiveEndDate(feeConfig.getEffectiveEndDate());
        existing.setRemark(feeConfig.getRemark());
        updateById(existing);
    }

    @Override
    public void toggleStatus(Long id) {
        FeeConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("费用项不存在");
        }
        config.setStatus(config.getStatus() == 1 ? 0 : 1);
        updateById(config);
    }
}