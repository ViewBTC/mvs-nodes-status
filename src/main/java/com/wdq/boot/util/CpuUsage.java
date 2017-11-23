package com.wdq.boot.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/10/18 0018.
 * cpu使用率
 */
public class CpuUsage implements Callable {

    private static Logger log = Logger.getLogger("CpuUsage");

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);

    /**
     * Purpose:采集CPU使用率
     *
     * @return float, CPU使用率, 小于1
     */
    public BigDecimal get() {
        Map<String, Object> map = new HashMap<String, Object>();
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        try {
            inputs = new InputStreamReader(new FileInputStream("/proc/meminfo"));
            buffer = new BufferedReader(inputs);
            String line = "";
            while (true) {
                line = buffer.readLine();
                if (line == null)
                    break;
                int beginIndex = 0;
                int endIndex = line.indexOf(":");
                if (endIndex != -1) {
                    String key = line.substring(beginIndex, endIndex);
                    beginIndex = endIndex + 1;
                    endIndex = line.length();
                    String memory = line.substring(beginIndex, endIndex);
                    String value = memory.replace("kB", "").trim();
                    map.put(key, value);
                }
            }

            long memTotal = Long.parseLong(map.get("MemTotal").toString());
            long memFree = Long.parseLong(map.get("MemFree").toString());
            long memused = memTotal - memFree;
            long buffers = Long.parseLong(map.get("Buffers").toString());
            long cached = Long.parseLong(map.get("Cached").toString());
            float usage = (float) (memused - buffers - cached) / memTotal;
            return new BigDecimal(usage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return BigDecimal.ZERO;
    }


    public BigDecimal call() throws Exception {
        return get();
    }

    public static BigDecimal getResult() throws ExecutionException, InterruptedException {
        Future<BigDecimal> future = scheduledExecutorService.submit(new CpuUsage());
        return future.get();
    }
}
