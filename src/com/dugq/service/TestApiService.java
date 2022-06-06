package com.dugq.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dugq.bean.ResponseBean;
import com.dugq.component.testapi.MainPanel;
import com.dugq.component.testapi.TestApiPanel;
import com.dugq.exception.ErrorException;
import com.dugq.http.HttpExecuteService;
import com.dugq.pojo.FeignKeyValueBean;
import com.dugq.pojo.KeyValueBean;
import com.dugq.pojo.TestApiBean;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dugq on 2021/4/8.
 */
public final class TestApiService {

    private final Project project;

    protected TestApiService(Project project) {
        this.project = project;
    }

    public static TestApiService getInstance(Project project){
        return project.getService(TestApiService.class);
    }

    public void sendCurrentRequest(TestApiBean testApiBean, TestApiPanel testApiPanel) throws IOException {
        testApiPanel.clearResponse();
        final MainPanel mainPanel = testApiPanel.getMainContent();
        String host = mainPanel.getHost();
        if (StringUtils.isBlank(host)){
            throw new ErrorException("请填写host");
        }
        if (!host.startsWith("http")){
            host = "http://"+host;
        }
        String uri = testApiBean.getUri();
        String url = host + (uri.startsWith("/")?uri:("/"+uri));
        String requestMethod = testApiBean.getRequestType().getDesc().toUpperCase();
        Map<String, String> headerMap = testApiPanel.getHeaders();
        ResponseBean responseBean;
        if (StringUtils.equals(requestMethod,"GET")){
            final Map<String, String> paramMap = dealRequestParamMap(testApiBean, testApiPanel);
            responseBean = HttpExecuteService.sendGet(url, headerMap, paramMap);
        }else if (StringUtils.equals(requestMethod,"POST")){
            String requestBody = dealRequestBody(testApiBean.getApiParamBean().getRequestBody(),testApiPanel);
            responseBean = HttpExecuteService.doPost(url,headerMap,requestBody);
        }else{
            throw new ErrorException( "HTTP Method is not selected!!!");
        }
        if (!responseBean.isSuccess()){
            throw new ErrorException("response status = {} "+responseBean.getStatus());
        }
        mainPanel.clearAndPrintResponse(responseBean.getResponseBody());
    }

    private Map<String,String> dealRequestParamMap(TestApiBean requestParams, TestApiPanel testApiPanel) {
        Map<String,String> paramMap = new HashMap<>();
        Map<String, String> globalParamMap = testApiPanel.getGlobalParamMap();
        for (KeyValueBean requestParam : requestParams.getApiParamBean().getRequestParams()) {
            if (StringUtils.isBlank(requestParam.getValue())){
                final String value = globalParamMap.get(requestParam.getKey());
                if (StringUtils.isNotBlank(value)){
                    paramMap.put(requestParam.getKey(), value);
                }
            }else{
                paramMap.put(requestParam.getKey(),requestParam.getValue());
            }
        }
        for (FeignKeyValueBean requestParam : requestParams.getApiParamBean().getFeignKeyValueBeans()) {
            if (Objects.isNull(requestParam.getIndex())){
                continue;
            }
            if (StringUtils.isBlank(requestParam.getValue())){
                paramMap.put("_p"+requestParam.getIndex(),globalParamMap.get(requestParam.getKey()));
            }else{
                paramMap.put("_p"+requestParam.getIndex(),requestParam.getValue());
            }
        }
        return paramMap;
    }

    public String dealRequestBody(String requestBody, TestApiPanel testApiPanel) {
//        if (StringUtils.isBlank(requestBody)){
//            return null;
//        }
//        try {
//            JSONObject jsonObject = JSONObject.parseObject(requestBody);
//            Map<String, String> globalParamMap = testApiPanel.getGlobalParamMap();
//            putDefaultValue(jsonObject,globalParamMap);
//            return jsonObject.toJSONString();
//        }catch (Exception e){
            return requestBody;
//        }
    }

    private void putDefaultValue(JSONObject jsonObject, Map<String, String> globalParamMap) {
        for (String key : jsonObject.keySet()) {
            final Object jsonValue = jsonObject.get(key);
            if (jsonValue instanceof JSONArray){
                final JSONArray jsonArray = (JSONArray) jsonValue;
                for (Object object : jsonArray) {
                    if (object instanceof JSONObject){
                        putDefaultValue((JSONObject)object,globalParamMap);
                    }
                }
            }else if(jsonValue instanceof JSONObject){
                putDefaultValue((JSONObject)jsonValue,globalParamMap);
            }else{
                if (StringUtils.isBlank(jsonObject.getString(key))){
                    jsonObject.put(key,globalParamMap.get(key));
                }
            }
        }
    }

}
