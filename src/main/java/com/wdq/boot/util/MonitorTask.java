package com.wdq.boot.util;

import com.wdq.boot.service.RedisService;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class MonitorTask implements Runnable {

    private RedisService redisService;
    private String ip;

    public MonitorTask(RedisService redisService, String ip) {
        this.redisService = redisService;
        this.ip = ip;
    }

    public void run() {//检测cpu，io，磁盘，网络带宽使用情况
        try {
            while (true) {
                BigDecimal cpuUsage = CpuUsage.getResult();
                BigDecimal ioUsage = IoUsage.getResult();
                BigDecimal netUsage = NetUsage.getResult();
                BigDecimal diskFree = Diskfree.getResult();
                if (cpuUsage != null) {
                    redisService.set("ip:" + ip + ":cpuUsage", cpuUsage.toString());
                }
                if (ioUsage != null) {
                    redisService.set("ip:" + ip + ":ioUsage", ioUsage.toString());
                }
                if (netUsage != null) {
                    redisService.set("ip:" + ip + ":netUsage", netUsage.toString());
                }

                if (diskFree != null) {
                    redisService.set("ip:" + ip + ":diskFree", diskFree.toString());
                }
                TimeUnit.MINUTES.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
