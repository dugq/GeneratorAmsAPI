package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.dugq.bean.ResponseBean;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.yapi.ResultBean;
import com.dugq.pojo.yapi.YapiConfigBean;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dugq
 * @date 2021/11/11 8:35 下午
 */
public class YapiAdminService extends YapiUserService implements YapiBaseService{
    private final YapiProjectService yapiProjectService;

    public YapiAdminService(Project project) {
        super(project);
        yapiProjectService=project.getService(YapiProjectService.class);
    }

    protected YapiConfigBean getYapiConfigBean() {
        YapiConfigBean yapiConfigBean = new YapiConfigBean();
        yapiConfigBean.setEmail("admin@admin.com");
        yapiConfigBean.setPassword("ymfe.org");
        return yapiConfigBean;
    }

    public String getCookie(){
        if (StringUtils.isBlank(cookie)){
            return cookie = login(false);
        }
        return cookie;
    }

    public boolean addCurrentProjectDeveloperPermission(){
        YapiConfigBean yapiConfigBean = configService.read();
        Map<String,Object> params = new HashMap<>();
        params.put("id",String.valueOf(yapiProjectService.getCurrentProjectId()));
        params.put("member_uids",new String[]{String.valueOf(yapiConfigBean.getUserId())});
        params.put("role","dev");
        try {
            final ResponseBean responseBean = sendPost(UrlFactory.addProjectPermissionUrl, params);
            final String responseBody = responseBean.getResponseBody();
            ResultBean result = JSON.parseObject(responseBody,ResultBean.class);
            return result.isSuccess();
        } catch (IOException e) {
            throw new ErrorException("获取项目列表失败");
        }
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public YapiUserService getUserService() {
        return this;
    }

    @Override
    public void refreshToken() {
        this.cookie = login(false);
    }
}
