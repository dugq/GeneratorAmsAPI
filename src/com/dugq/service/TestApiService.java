package com.dugq.service;

import com.alibaba.fastjson.JSONObject;
import com.dugq.bean.ResponseBean;
import com.dugq.component.TestApiPanel;
import com.dugq.http.HttpExecuteService;
import com.dugq.util.ErrorPrintUtil;
import com.dugq.util.MyRandomUtils;
import com.dugq.util.TestApiUtil;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

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


    public void sendCurrentRequest() {
        TestApiPanel testApiPanel = TestApiUtil.getTestApiPanel(this.project);
        String host = testApiPanel.getHost();
        String uri = testApiPanel.getUri();
        String url = host + (uri.startsWith("/")?uri:("/"+uri));
        String requestMethod = testApiPanel.getRequestMethod();
        String requestParam = testApiPanel.getRequestParam();
        Map<String, String> headerMap = testApiPanel.getHeaderMap();
        ResponseBean responseBean;
        try {
            if (StringUtils.equals(requestMethod,"GET")){
                responseBean = HttpExecuteService.doGetWithJSONString(url, headerMap, requestParam);
            }else if (StringUtils.equals(requestMethod,"POST")){
                responseBean = HttpExecuteService.doPostWithJSONString(url,headerMap,requestParam);
            }else{
                ErrorPrintUtil.printLine("HTTP Method is not selected!!!",project);
                return;
            }
        }catch (Exception e){
            ErrorPrintUtil.printLine(e.getMessage(),project);
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                ErrorPrintUtil.printLine("\tat   "+stackTraceElement.getClassName()+"#"+stackTraceElement.getMethodName()+"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")",project);
            }
            return;
        }
        if (!responseBean.isSuccess()){
            ErrorPrintUtil.printLine("response status = {} "+responseBean.getStatus(),project);
            return;
        }
        testApiPanel.printResponse(responseBean.getResponseBody());
    }

    public JSONObject dealRequestParam(JSONObject request) {
        TestApiPanel testApiPanel = TestApiUtil.getTestApiPanel(this.project);
        Map<String, String> globalParamMap = testApiPanel.getGlobalParamMap();
        if (MapUtils.isEmpty(request)){
            return request;
        }
        for (Map.Entry<String, Object> entry : request.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String){
                fillPrimitiveValue(request, globalParamMap, key, (String) value);
            }else{
                Class<?> aClass = value.getClass();
                System.out.println(aClass.getName());
            }
        }
        return request;
    }

    protected void fillPrimitiveValue(JSONObject request, Map<String, String> globalParamMap, String key, String value) {
        String globalValue = globalParamMap.get(key);
        if (StringUtils.isNotBlank(globalValue)){
            request.put(key,globalValue);
        }else{
            String type = value;
            if (type.contains("$&")){
                type = type.substring(0,type.indexOf("$&"));
            }
            switch (type){
                case "long":request.put(key, MyRandomUtils.randomLong());break;
                case "int":request.put(key, MyRandomUtils.randomInt());break;
                case "boolean":request.put(key,MyRandomUtils.randomBoolean());break;
                case "String":request.put(key,MyRandomUtils.randomString());break;
                default:System.out.println("type is not support! = "+type);
            }
        }
    }
}
