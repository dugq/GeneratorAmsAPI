package com.dugq.service.config.impl;

import com.dugq.pojo.yapi.YapiConfigBean;
import com.dugq.service.config.AbstractSingleValueConfigService;
import com.dugq.service.yapi.UrlFactory;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;

/**
 * @author dugq
 * @date 2021/8/11 6:50 下午
 */
public class YapiConfigService extends AbstractSingleValueConfigService<YapiConfigBean> {

    public static final String yapiConfigFile = "/yapi.txt";

    public YapiConfigService(Project project) {
        super(project,yapiConfigFile,YapiConfigBean.class);
    }

    public String getHost(){
        final String server = read().getServer();
        if (StringUtils.isBlank(server)){
            return UrlFactory.host;
        }
        return server;
    }
}
