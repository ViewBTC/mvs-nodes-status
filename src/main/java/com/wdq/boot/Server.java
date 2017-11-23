/*
package com.wdq.boot;

import com.radarwin.framework.util.PropertyReader;
import com.wdq.boot.server.NettyClient;
import com.wdq.boot.util.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

*/
/**
 * Created by Administrator on 2017/10/18 0018.
 *//*

public class Server implements ApplicationContextAware, ApplicationListener {
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void onApplicationEvent(ApplicationEvent event) {

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        String ipStr = PropertyReader.get("ips", "hostName.properties");
        String[] ips = ipStr.split(",");
        for (String ip : ips) {
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            executorService.execute(new NettyClient(inetAddress, 5251));
            System.out.println("ip:" + ip);
        }
        //检测cpu，io，磁盘，网络带宽使用情况
        executorService.execute(new MonitorTask());
    }

}
*/
