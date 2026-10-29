package com.library.service;

import java.util.List;
import java.util.Map;

/**
 * 统计服务（FR-RPT-001/002）。
 */
public interface StatsService {

    /** 借阅排行（可按天数过滤，默认全部） */
    List<Map<String, Object>> topBorrowed(Integer days, int limit);

    /** 近 N 个月的月度借还统计 */
    List<Map<String, Object>> monthlyCount(int months);
}
