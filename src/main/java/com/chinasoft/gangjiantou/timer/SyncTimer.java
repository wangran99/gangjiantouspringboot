package com.chinasoft.gangjiantou.timer;

import com.chinasoft.gangjiantou.service.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class SyncTimer {
    @Autowired
    SyncService syncService;

    //每隔1小时
//    @Scheduled(fixedRate = 24 * 60 * 60 * 1000, initialDelay = 2000)
    @Scheduled(cron = "0 0 0 * * ?")//每天晚上0点执行
    @Transactional
    public void scheduled() {
        syncService.delDepts();
        syncService.syncDepts();
        syncService.syncUsers();
    }
}
