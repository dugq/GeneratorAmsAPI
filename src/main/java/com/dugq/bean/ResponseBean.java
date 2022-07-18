package com.dugq.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dugq.pojo.yapi.ResultBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by dugq on 2021/4/8.
 */
public class ResponseBean {

    private Integer status;
    private String responseBody;
    private List<Header> headerList;

    public ResponseBean(Integer status, Header[] allHeaders) {
        this(status,null,allHeaders);
    }

    public ResponseBean(int status, String responseBody, Header[] allHeaders) {
        this.status = status;
        this.responseBody = responseBody;
        if (!ArrayUtils.isEmpty(allHeaders)){
            headerList = Arrays.asList(allHeaders);
        }
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


    public<T> ResultBean<T> getObjectData(Class<T> clazz){
        return JSON.parseObject(getResponseBody(), new TypeReference<ResultBean<T>>(clazz){});
    }

    public<T> ResultBean<List<T>> getListData(Class<T> clazz){
        return JSON.parseObject(getResponseBody(), new TypeReference<ResultBean<List<T>>>(clazz){});
    }

    public String getCookies(){
        if (CollectionUtils.isEmpty(headerList)){
            return null;
        }
        return headerList.stream()
                .filter(header -> StringUtils.equals("Set-Cookie",header.getName()))
                .map(header -> header.getValue().substring(0,header.getValue().indexOf(";")))
                .collect(Collectors.joining(";"));
    }
}
