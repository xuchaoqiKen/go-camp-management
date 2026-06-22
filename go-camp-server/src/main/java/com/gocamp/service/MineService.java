package com.gocamp.service;

import com.gocamp.vo.MineProfileVO;

public interface MineService {
    MineProfileVO getProfile(Long userId);
}