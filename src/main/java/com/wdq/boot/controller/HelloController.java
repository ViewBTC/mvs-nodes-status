package com.wdq.boot.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.wdq.boot.model.Seed;
import com.wdq.boot.service.RedisService;
import com.wdq.boot.util.AddressUtils;
import com.wdq.boot.util.ResponseUtils;
import com.wdq.boot.util.ResponseWrapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
@RestController
public class HelloController {

    @Autowired
    private RedisService redisService;

    @RequestMapping("monitorPeer")
    @ResponseBody
    //节点信息
    public ResponseWrapper monitor() {
        Set<String> sets = redisService.getAllKeys();
        ResponseWrapper responseWrapper = ResponseUtils.successResponse("");
        List<Seed> list = new ArrayList<Seed>();
        if (sets != null && sets.size() > 0) {
            for (String set : sets) {
                if (set.contains("node:good:")) {
                    getSeed(list, set, 0);
                } else if (set.contains("node:inaction:")) {
                    getSeed(list, set, 1);
                }
            }
        }
        responseWrapper.addAttribute("seeds", list);
        return responseWrapper;
    }

    private void getSeed(List<Seed> list, String set, int status) {
        Seed seed = new Seed();
        String ip = (String) redisService.get(set);
        String address = (String) redisService.get(ip);
        seed.setHost(ip);
        seed.setAddress(address);
        seed.setStatus(status);
        list.add(seed);
    }


}
