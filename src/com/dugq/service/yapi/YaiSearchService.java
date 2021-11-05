package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dugq.bean.ResponseBean;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.yapi.ResultBean;
import com.dugq.pojo.yapi.YapiSearchResult;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dugq
 * @date 2021/8/11 9:28 下午
 */
public class YaiSearchService extends YapiBaseService{
    private final Project project;

    public YaiSearchService(Project project) {
        this.project = project;
    }

    public YapiSearchResult search(String content){
        Map<String,String> params = new HashMap<>();
        params.put("q",content);
        try {
            final ResponseBean responseBean = sendGet(UrlFactory.searchUrl, params);
            final String responseBody = responseBean.getResponseBody();
            ResultBean<YapiSearchResult> result = JSON.parseObject(responseBody, new TypeReference<ResultBean<YapiSearchResult>>(){});
            if (!result.isSuccess()){
                throw new ErrorException("查询失败 errorcode="+result.getErrcode()+"errmsg="+result.getErrmsg());
            }
            return result.getData();
        } catch (IOException e) {
            throw new ErrorException("查询失败");
        }
    }

    @Override
    Project getProject() {
        return project;
    }
}
