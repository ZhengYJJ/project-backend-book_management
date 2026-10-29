package com.library.service.impl;

import com.library.mapper.LoanMapper;
import com.library.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private LoanMapper loanMapper;

    @Override
    public List<Map<String, Object>> topBorrowed(Integer days, int limit) {
        if (days != null && (days <= 0 || days > 3650)) {
            days = null;
        }
        int l = Math.max(1, Math.min(limit, 100));
        return loanMapper.selectTopBorrowed(days, l);
    }

    @Override
    public List<Map<String, Object>> monthlyCount(int months) {
        int m = Math.max(1, Math.min(months, 60));
        return loanMapper.selectMonthlyCount(m);
    }
}
