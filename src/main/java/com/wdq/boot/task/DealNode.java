package com.wdq.boot.task;

import com.wdq.boot.server.NettyClient;
import com.wdq.boot.service.RedisService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by Administrator on 2017/11/10 0010.
 */
public class DealNode implements Runnable {

    private RedisService redisCache;
    private ExecutorService executorService;

    public DealNode(RedisService redisCache, ExecutorService executorService) {
        this.redisCache = redisCache;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        Set<String> nodes = redisCache.getAllKeys();
        for (String node : nodes) {
            if (node.contains("node:") && !node.contains("node:good:") && !node.contains("node:inaction:")) {
                node = (String) redisCache.get(node);
                InetAddress ipAddress = null;
                try {
                    ipAddress = InetAddress.getByName(node);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                executorService.execute(new NettyClient(ipAddress, 5251, redisCache));
            }
        }
    }
}
