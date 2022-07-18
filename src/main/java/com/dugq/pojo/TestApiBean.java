package com.dugq.pojo;

import com.dugq.pojo.enums.RequestType;

/**
 * @author dugq
 * @date 2021/7/13 3:28 下午
 */
public class TestApiBean {

    private String name;

    private String host;

    private String uri;

    private RequestType requestType;

    private TestApiParamBean apiParamBean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public TestApiParamBean getApiParamBean() {
        return apiParamBean;
    }

    public void setApiParamBean(TestApiParamBean apiParamBean) {
        this.apiParamBean = apiParamBean;
    }
}
