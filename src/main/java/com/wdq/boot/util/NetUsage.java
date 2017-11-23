package com.wdq.boot.util;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/10/18 0018.
 * 网络带宽使用率情况
 */
public class NetUsage implements Callable {
    private final static float TotalBandwidth = 1000;    //网口带宽,Mbps
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);

    /**
     * @return float, 网络带宽使用率, 小于1
     * @Purpose:采集网络带宽使用率
     */
    public BigDecimal get() {
        float TotalBandwidth = 1000;
        float netUsage = 0.0f;
        Process pro1, pro2;
        Runtime r = Runtime.getRuntime();
        Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        try {
            String command = "cat /proc/net/dev";
            //第一次采集流量数据
            long startTime = System.currentTimeMillis();
            pro1 = r.exec(command);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line = null;
            long inSize1 = 0, outSize1 = 0;
            while ((line = in1.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("eth0")) {
                    String[] temp = line.split("\\s+");
                    inSize1 = Long.parseLong(temp[1].substring(5)); //Receive bytes,单位为Byte
                    outSize1 = Long.parseLong(temp[9]);             //Transmit bytes,单位为Byte
                    break;
                }
            }
            in1.close();
            pro1.destroy();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
            }
            //第二次采集流量数据
            long endTime = System.currentTimeMillis();
            pro2 = r.exec(command);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
            long inSize2 = 0, outSize2 = 0;
            while ((line = in2.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("eth0")) {
                    String[] temp = line.split("\\s+");
                    inSize2 = Long.parseLong(temp[1].substring(5));
                    outSize2 = Long.parseLong(temp[9]);
                    break;
                }
            }
            if (inSize1 != 0 && outSize1 != 0 && inSize2 != 0 && outSize2 != 0) {
                float interval = (float) (endTime - startTime) / 1000;
                //网口传输速度,单位为bps
                float curRate = (float) (inSize2 - inSize1 + outSize2 - outSize1) * 8 / (1000000 * interval);
                netUsage = curRate / TotalBandwidth;
            }
            in2.close();
            pro2.destroy();
            return new BigDecimal(netUsage);
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal call() throws Exception {
        return get();
    }

    public static BigDecimal getResult() throws ExecutionException, InterruptedException {
        Future<BigDecimal> future = scheduledExecutorService.submit(new NetUsage());
        return future.get();
    }
}
