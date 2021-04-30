package io.renren.timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SyncTimer {
    @Autowired
    SyncService syncService;
    //每隔两分钟
    @Scheduled(fixedRateString = "120000",initialDelay = 2000)
    public void scheduled() {
        syncService.delDepts();
        syncService.syncDepts();
        syncService.syncUsers();
    }
}
