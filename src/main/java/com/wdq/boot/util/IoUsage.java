package com.wdq.boot.util;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
public class IoUsage implements Callable {

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);

    /**
     * @return float, 磁盘IO使用率, 小于1
     * @Purpose:采集磁盘IO使用率
     */
    public BigDecimal get() {
        Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        float ioUsage = 0.0f;
        Process pro = null;
        Runtime r = Runtime.getRuntime();
        try {
            String command = "iostat -d -x";
            pro = r.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            int count = 0;
            while ((line = in.readLine()) != null) {
                if (++count >= 4) {
//					log.info(line);
                    String[] temp = line.split("\\s+");
                    if (temp.length > 1) {
                        float util = Float.parseFloat(temp[temp.length - 1]);
                        ioUsage = (ioUsage > util) ? ioUsage : util;
                    }
                }
            }
            if (ioUsage > 0) {
                ioUsage /= 100;
            }
            in.close();
            pro.destroy();
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigDecimal(ioUsage);
    }

    public BigDecimal call() throws Exception {
        return get();
    }

    public static BigDecimal getResult() throws ExecutionException, InterruptedException {
        Future<BigDecimal> future = scheduledExecutorService.submit(new IoUsage());
        return future.get();
    }
}
