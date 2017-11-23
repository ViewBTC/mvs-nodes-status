package com.wdq.boot;

import com.wdq.boot.server.NettyClient;
import com.wdq.boot.service.RedisService;
import com.wdq.boot.task.DealNode;
import com.wdq.boot.util.DnsDiscovery;
import com.wdq.boot.util.PropertyReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class Application {

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        RedisService redisCache = applicationContext.getBean(RedisService.class);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8);
        String ipStr = PropertyReader.get("ips", "hostName.properties");
        String[] ips = ipStr.split(",");
        for (String ip : ips) {
            System.out.println("ip:" + ip);
            InetAddress inetAddress = InetAddress.getByName(ip);
            executorService.execute(new NettyClient(inetAddress, 5251, redisCache));
        }
        List<InetAddress> inetAddresses = DnsDiscovery.getPeers();
        for (InetAddress inetAddress : inetAddresses) {
            executorService.execute(new NettyClient(inetAddress, 5251, redisCache));
        }
        //定时任务处理
        executorService.scheduleWithFixedDelay(new DealNode(redisCache, executorService), 0, 30, TimeUnit.SECONDS);
    }
}
