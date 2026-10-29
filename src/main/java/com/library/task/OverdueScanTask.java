package com.library.task;

import com.library.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 逾期扫描定时任务（FR-CIRC-005）：
 * 每日 01:00 将在借且超过应还日期的记录置为逾期，并按读者类型费率累计滞纳金。
 */
@Component
public class OverdueScanTask {

    private static final Logger log = LoggerFactory.getLogger(OverdueScanTask.class);

    @Autowired
    private LoanService loanService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void scan() {
        try {
            int count = loanService.scanOverdue();
            log.info("[逾期扫描] 完成，本次处理 {} 条逾期在借记录", count);
        } catch (Exception e) {
            log.error("[逾期扫描] 执行失败", e);
        }
    }
}
