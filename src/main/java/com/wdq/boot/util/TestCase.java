package com.wdq.boot.util;


import java.util.Set;


public class TestCase {
    public static void main(String[] args) {
        String ips = "/198.199.84.199:5251";
        String ip = ips.substring(1, 14);
        System.out.println(new String("ping").getBytes());
        System.out.println(new String("pong").getBytes());
    }
}
