package com.dugq.pojo;

import com.dugq.pojo.enums.RequestType;

import java.util.List;

/**
 * @author dugq
 * @date 2021/7/6 9:22 下午
 */

public class ApiBean {

    private boolean isRpc = false;
    /**
     * 参数列表
     */
    private List<ParamBean> apiParamBean;

    private List<ParamBean> feignParamBean;
    /**
     * response模版
     */
    private List<ParamBean> apiResultParam;
    /**
     * uri
     */
    private String apiURI;
    /**
     * 名称
     */
    private String apiName;
    /**
     * 请求类型
     */
    private RequestType apiRequestType = RequestType.post;

    private String desc;

    private Long menuId;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public RequestType getApiRequestType() {
        return apiRequestType;
    }

    public void setApiRequestType(RequestType apiRequestType) {
        this.apiRequestType = apiRequestType;
    }

    public List<ParamBean> getApiParamBean() {
        return apiParamBean;
    }

    public void setApiParamBean(List<ParamBean> apiParamBean) {
        this.apiParamBean = apiParamBean;
    }

    public List<ParamBean> getApiResultParam() {
        return apiResultParam;
    }

    public void setApiResultParam(List<ParamBean> apiResultParam) {
        this.apiResultParam = apiResultParam;
    }

    public String getApiURI() {
        return apiURI;
    }

    public void setApiURI(String apiURI) {
        this.apiURI = apiURI;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public boolean isRpc() {
        return isRpc;
    }

    public void setRpc(boolean rpc) {
        isRpc = rpc;
    }

    public List<ParamBean> getFeignParamBean() {
        return feignParamBean;
    }

    public void setFeignParamBean(List<ParamBean> feignParamBean) {
        this.feignParamBean = feignParamBean;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
