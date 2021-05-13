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
    //每隔两分钟
    @Scheduled(fixedRateString = "120000",initialDelay = 2000)
    @Transactional
    public void scheduled() {
        syncService.delDepts();
        syncService.syncDepts();
        syncService.syncUsers();
    }
}
