package com.wdq.boot.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/17 0017.
 */
public class DnsDiscovery {

    private static String[] hostNames = {"main-asia.metaverse.live",
            "main-americas.metaverse.live",
            "main-europe.metaverse.live",
            "main-asia.mvs.live",
            "main-americas.mvs.live",
            "main-europe.mvs.live",
            "seed.getmvs.org"
    };


    public static InetAddress[] getPeer(String hostname) {
        try {
            InetAddress[] response = InetAddress.getAllByName(hostname);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<InetAddress> getPeers() {
        List<InetAddress> inetAddresses = new ArrayList<InetAddress>();
        for (String hostname : hostNames) {
            InetAddress[] InetAddress = getPeer(hostname);
            for (InetAddress inetAddress : InetAddress) {
                inetAddresses.add(inetAddress);
            }
        }
        return inetAddresses;
    }


    public static void main(String[] args) {
        System.out.println(DnsDiscovery.getPeers());
    }


}
