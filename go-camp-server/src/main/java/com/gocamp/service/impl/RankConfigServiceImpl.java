package com.gocamp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gocamp.common.BusinessException;
import com.gocamp.entity.RankConfig;
import com.gocamp.mapper.RankConfigMapper;
import com.gocamp.service.RankConfigService;
import org.springframework.stereotype.Service;

@Service
public class RankConfigServiceImpl extends ServiceImpl<RankConfigMapper, RankConfig> implements RankConfigService {

    @Override
    public void createRank(RankConfig rankConfig) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<RankConfig>()
                .eq(RankConfig::getRankCode, rankConfig.getRankCode()));
        if (count > 0) {
            throw new BusinessException("段位编码已存在");
        }
        rankConfig.setStatus(1);
        save(rankConfig);
    }

    @Override
    public void updateRank(RankConfig rankConfig) {
        RankConfig existing = getById(rankConfig.getId());
        if (existing == null) {
            throw new BusinessException("段位配置不存在");
        }
        existing.setRankName(rankConfig.getRankName());
        existing.setRankCode(rankConfig.getRankCode());
        existing.setCategoryName(rankConfig.getCategoryName());
        existing.setRankRange(rankConfig.getRankRange());
        existing.setSortOrder(rankConfig.getSortOrder());
        updateById(existing);
    }

    @Override
    public void toggleStatus(Long id) {
        RankConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("段位配置不存在");
        }
        config.setStatus(config.getStatus() == 1 ? 0 : 1);
        updateById(config);
    }
}