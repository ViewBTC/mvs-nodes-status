package com.wdq.boot.util;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/19 0019.
 */
public class ResponseWrapper implements Serializable {
    private String msg;
    private boolean success;
    private int status;
    private java.util.Map<java.lang.String, java.lang.Object> dataWrapper;

    public void addAttribute(String key, Object object) {
        dataWrapper = new HashMap<>();
        dataWrapper.put(key, object);
    }

    public Map<String, Object> getDataWrapper() {
        return dataWrapper;
    }

    public void setDataWrapper(Map<String, Object> dataWrapper) {
        this.dataWrapper = dataWrapper;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
