package com.dugq.requestmapping.mapping.bean;

import com.dugq.requestmapping.param.bean.ParamBean;

import java.util.List;

/**
 * Created by dugq on 2021/4/19.
 */
public class RequestMapping {

    private Integer reqType;

    private String uri;

    private List<ParamBean> paramBeanList;


    public Integer getReqType() {
        return reqType;
    }

    public void setReqType(Integer reqType) {
        this.reqType = reqType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<ParamBean> getParamBeanList() {
        return paramBeanList;
    }

    public void setParamBeanList(List<ParamBean> paramBeanList) {
        this.paramBeanList = paramBeanList;
    }
}
