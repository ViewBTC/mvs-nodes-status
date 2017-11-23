package com.wdq.boot.task;

import com.wdq.boot.server.NettyClient;
import com.wdq.boot.service.RedisService;
import com.wdq.boot.util.AddressUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by Administrator on 2017/11/10 0010.
 */
public class DealNodeIp implements Runnable {

    private RedisService redisCache;

    public DealNodeIp(RedisService redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public void run() {
        Set<String> nodes = redisCache.getAllKeys();
        for (String node : nodes) {
            if (node.contains("node:good:") || node.contains("node:inaction:")) {
                try {
                    node = (String) redisCache.get(node);
                    System.out.println("===============处理ip:" + node + "=====================");
                    String address = AddressUtils.getAddresses("ip=" + node, "utf-8");
                    if (StringUtils.isNotEmpty(address) && !"0".equals(address)) {
                        JSONObject jsonObject = new JSONObject(address);
                        int code = jsonObject.getInt("code");
                        if (0 == code) {
                            address = (jsonObject.getJSONObject("data")).getString("country");
                            if (!redisCache.exists(node)) {
                                redisCache.set(node, address);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
