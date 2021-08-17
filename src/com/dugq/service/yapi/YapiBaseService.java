package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.dugq.bean.ResponseBean;
import com.dugq.http.HttpExecuteService;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dugq
 * @date 2021/8/11 7:56 下午
 */
public abstract class YapiBaseService {

    public ResponseBean sendPost( String url,Object param) throws IOException {
        YapiUserService userService = getProject().getService(YapiUserService.class);
        Map<String,String> headers = new HashMap<>();
        try {
            headers.put("Cookie",userService.getCookie());
            return HttpExecuteService.doPost(UrlFactory.host +url, headers, JSON.toJSONString(param));
        }catch (IOException e){
            userService.refreshToken();
            headers.put("Cookie",userService.getCookie());
            return HttpExecuteService.doPost(UrlFactory.host +url, headers, JSON.toJSONString(param));
        }
    }

    public ResponseBean sendGet(String url,Map<String,String> paramMap)throws IOException{
        YapiUserService userService = getProject().getService(YapiUserService.class);
        try {
            Map<String,String> headers = new HashMap<>();
            headers.put("Cookie",userService.getCookie());
            return HttpExecuteService.sendGet(UrlFactory.host +url, headers,paramMap);
        }catch (IOException e){
            userService.refreshToken();
            Map<String,String> headers = new HashMap<>();
            headers.put("Cookie",userService.getCookie());
            return HttpExecuteService.sendGet(UrlFactory.host +url, headers,paramMap);
        }
    }

    abstract Project getProject();

}
