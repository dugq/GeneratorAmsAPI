package com.dugq.pojo;

import java.util.List;

/**
 * @author dugq
 * @date 2021/7/13 8:23 下午
 */
public class TestApiParamBean {
    public static final Integer PARAM_MAP_MODEL = 1;
    public static final Integer BODY_JSON_MODEL = 2;
    public static final Integer FEIGN_JSON_MODEL = 3;

    //1:params 2:body
    private Integer model;

    private List<KeyValueBean> requestParams;

    private String requestBody;

    private List<FeignKeyValueBean> feignKeyValueBeans;

    public List<FeignKeyValueBean> getFeignKeyValueBeans() {
        return feignKeyValueBeans;
    }

    public void setFeignKeyValueBeans(List<FeignKeyValueBean> feignKeyValueBeans) {
        this.feignKeyValueBeans = feignKeyValueBeans;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    public List<KeyValueBean> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<KeyValueBean> requestParams) {
        this.requestParams = requestParams;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
