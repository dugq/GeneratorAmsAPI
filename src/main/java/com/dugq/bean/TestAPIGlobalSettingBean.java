package com.dugq.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dugq on 2021/4/8.
 */
public class TestAPIGlobalSettingBean implements Serializable {
    private static final long serialVersionUID = 6500038264168215521L;

    private String host;

    private Map<String,String> globalParam;

    private Map<String,String> headerMap;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, String> getGlobalParam() {
        return globalParam;
    }

    public void setGlobalParam(Map<String, String> globalParam) {
        this.globalParam = globalParam;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }
}
