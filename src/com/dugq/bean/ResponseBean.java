package com.dugq.bean;

import java.util.Objects;

/**
 * Created by dugq on 2021/4/8.
 */
public class ResponseBean {

    private Integer status;
    private String responseBody;

    public ResponseBean(Integer status) {
        this.status = status;
    }

    public ResponseBean(int status, String responseBody) {
        this.status = status;
        this.responseBody = responseBody;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public boolean isSuccess(){
        return Objects.nonNull(status) && status >=200 && status < 300;
    }
}
