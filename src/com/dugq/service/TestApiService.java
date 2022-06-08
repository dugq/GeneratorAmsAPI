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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

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

    public void sendCurrentRequest(TestApiBean testApiBean, TestApiPanel testApiPanel){
        testApiPanel.clearResponse();
        final MainPanel mainPanel = testApiPanel.getOrCreateSelectedMainContent();
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
        //开启线程进行RPC，当前线程属于UI线程，在线程执行中，页面处于暂停状态，如果RPC卡死，那么UI也会卡死
        new Thread(()->{
            try {
                final  ResponseBean responseBean = doSendRequest(testApiBean, testApiPanel, url, requestMethod, headerMap);
                if (!responseBean.isSuccess()){
                    throw new ErrorException("response status = {} "+responseBean.getStatus());
                }
                //重新交给UI thread 才可以进行UI操作，否则，将导致UI component失去控制
                ApplicationManager.getApplication().invokeLater(()-> mainPanel.clearAndPrintResponse(responseBean.getResponseBody()));
            } catch (Exception e) {
                ApplicationManager.getApplication().invokeLater(()-> mainPanel.printError(e.getMessage()));
            }
        }).start();
    }

    @NotNull
    public ResponseBean doSendRequest(TestApiBean testApiBean, TestApiPanel testApiPanel, String url, String requestMethod, Map<String, String> headerMap) throws IOException {
        ResponseBean responseBean;
        if (StringUtils.equals(requestMethod,"GET")){
            final Map<String, String> paramMap = dealRequestParamMap(testApiBean, testApiPanel);
            responseBean = HttpExecuteService.sendGet(url, headerMap, paramMap);
        }else if (StringUtils.equals(requestMethod,"POST")){
            String requestBody = dealRequestBody(testApiBean.getApiParamBean().getRequestBody(), testApiPanel);
            responseBean = HttpExecuteService.doPost(url, headerMap,requestBody);
        }else{
            throw new ErrorException( "HTTP Method is not selected!!!");
        }
        return responseBean;
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
