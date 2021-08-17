package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dugq.bean.ResponseBean;
import com.dugq.component.yapi.YapiLoginFormComponent;
import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.http.HttpExecuteService;
import com.dugq.pojo.yapi.LoginParam;
import com.dugq.pojo.yapi.LoginResult;
import com.dugq.pojo.yapi.ResultBean;
import com.dugq.pojo.yapi.YapiConfigBean;
import com.dugq.service.config.impl.YapiConfigService;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * @author dugq
 * @date 2021/8/11 4:30 下午
 */
public class YapiUserService {

    private String cookie;
    private final Project project;
    private final YapiConfigService configService;

    public YapiUserService(Project project) {
        this.project = project;
        configService = project.getService(YapiConfigService.class);
    }

    private String login() {
        YapiConfigBean configBean = getYapiConfigBean();
        LoginParam loginParam =getLoginParam(configBean);
        try {
            final ResponseBean responseBean = HttpExecuteService.doPost(UrlFactory.host +UrlFactory.loginUrl, null, JSON.toJSONString(loginParam));
            if (!responseBean.isSuccess()){
                throw new ErrorException("YAPI账号登陆失败。如账号密码错误，请在.idea"+YapiConfigService.yapiConfigFile+"中修改账号密码");
            }
            final String responseBody = responseBean.getResponseBody();
            ResultBean<LoginResult> result = JSON.parseObject(responseBody, new TypeReference<ResultBean<LoginResult>>(){});
            if (!result.isSuccess()){
                throw new ErrorException("YAPI账号登陆失败。code="+result.getErrmsg()+"msg="+result.getErrmsg());
            }
            configBean.setUserId(result.getData().getUserId());
            configService.save(configBean);
            return responseBean.getCookies();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErrorException("YAPI账号登陆失败");
        }
    }


    private LoginParam getLoginParam(YapiConfigBean configBean){
        LoginParam param = new LoginParam();
        param.setEmail(configBean.getEmail());
        param.setPassword(configBean.getPassword());
        return param;
    }

    @NotNull
    private YapiConfigBean getYapiConfigBean() {
        YapiConfigBean configBean = configService.read();
        if (Objects.isNull(configBean)){
            configBean = new YapiConfigBean();
        }
        if (Objects.isNull(configBean.getEmail())){
            letUserSetEmailAndPwd(configBean);
        }
        return configBean;
    }

    public String getCookie(){
        final YapiConfigBean configBean = configService.read();
        if (Objects.isNull(configBean) || StringUtils.isBlank(cookie)){
            return cookie = login();
        }
        return cookie;
    }

    public void refreshToken(){
        this.cookie = login();
    }

    private void letUserSetEmailAndPwd(YapiConfigBean configBean) {
        YapiLoginFormComponent loginForm = new YapiLoginFormComponent();
        boolean result = loginForm.showAndGet();
        if(!result){
            throw new StopException();
        }
        configBean.setEmail(loginForm.getEmail());
        configBean.setPassword(loginForm.getPassword());
    }
}
