package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.dugq.bean.ResponseBean;
import com.dugq.http.HttpExecuteService;
import com.dugq.service.config.impl.YapiConfigService;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dugq
 * @date 2021/8/11 7:56 下午
 */
public interface YapiBaseService {

    default ResponseBean sendPost( String url,Object param) throws IOException {
        YapiUserService userService = getUserService();
        Map<String,String> headers = new HashMap<>();
        try {
            headers.put("Cookie",userService.getCookie());
            return HttpExecuteService.doPost(getHost() +url, headers, JSON.toJSONString(param));
        }catch (IOException e){
            userService.refreshToken();
            headers.put("Cookie",userService.getCookie());
            return HttpExecuteService.doPost(getHost() +url, headers, JSON.toJSONString(param));
        }
    }

    default String getHost(){
        return getProject().getService(YapiConfigService.class).getHost();
    }

    default YapiUserService getUserService() {
        return getProject().getService(YapiUserService.class);
    }

    default ResponseBean sendGet(String url,Map<String,String> paramMap)throws IOException{
        YapiUserService userService = getUserService();
        try {
            Map<String,String> headers = new HashMap<>();
            headers.put("Cookie",userService.getCookie());
            return HttpExecuteService.sendGet(getHost() +url, headers,paramMap);
        }catch (IOException e){
            userService.refreshToken();
            Map<String,String> headers = new HashMap<>();
            headers.put("Cookie",userService.getCookie());
            return HttpExecuteService.sendGet(getHost() +url, headers,paramMap);
        }
    }

    Project getProject();

}
