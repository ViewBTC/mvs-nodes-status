package com.wdq.boot.util;

import org.apache.commons.io.FileSystemUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2017/10/18 0018.
 * 系统磁盘情况
 */
public class Diskfree implements Callable {

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);

    public static BigDecimal getDisk() {
        try {
            long total = FileSystemUtils.freeSpaceKb("/");
            double disk = (double) total / 1024 / 1024;
            return new BigDecimal(disk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal call() throws Exception {
        return getDisk();
    }

    public static BigDecimal getResult() throws ExecutionException, InterruptedException {
        Future<BigDecimal> future = scheduledExecutorService.submit(new Diskfree());
        return future.get();
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        while (true) {
            Thread.sleep(3000);
            System.out.println(Diskfree.getResult());
            System.out.println(CpuUsage.getResult());
            System.out.println(IoUsage.getResult());
            System.out.println(NetUsage.getResult());
        }
    }
}
