package com.wdq.boot.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
public class PropertyReader {
    private static Properties pps = null;

    static {
        pps = new Properties();
    }

    public static String get(String name, String fileName) {
        try {
            pps.load(PropertyReader.class.getClass().getResourceAsStream("/" + fileName));
            return pps.getProperty(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        System.out.println(PropertyReader.get("ips", "hostName.properties"));
    }

}
