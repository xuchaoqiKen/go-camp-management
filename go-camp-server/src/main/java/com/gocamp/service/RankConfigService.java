package com.gocamp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gocamp.entity.RankConfig;

public interface RankConfigService extends IService<RankConfig> {

    void createRank(RankConfig rankConfig);

    void updateRank(RankConfig rankConfig);

    void toggleStatus(Long id);
}