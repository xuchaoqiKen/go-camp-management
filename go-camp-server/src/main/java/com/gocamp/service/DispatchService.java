package com.gocamp.service;

import java.util.List;
import java.util.Map;

public interface DispatchService {

    Map<String, Object> getDailyStats(Long sessionId, String date);

    Map<String, Object> getDailyTable(Long sessionId, String startDate, String endDate, int page, int size);

    List<Map<String, Object>> getRankDistribution(Long sessionId, String date);
}
