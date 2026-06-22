package com.gocamp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gocamp.entity.FeeConfig;

public interface FeeConfigService extends IService<FeeConfig> {

    void createFee(FeeConfig feeConfig);

    void updateFee(FeeConfig feeConfig);

    void toggleStatus(Long id);
}