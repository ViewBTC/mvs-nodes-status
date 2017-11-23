package com.wdq.boot.model;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/10/18 0018.
 */
public class Seed {
    private String host;
    private String address;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
