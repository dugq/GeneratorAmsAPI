package com.dugq.service.config.impl;

import com.dugq.pojo.yapi.YapiConfigBean;
import com.dugq.service.config.AbstractSingleValueConfigService;
import com.intellij.openapi.project.Project;

/**
 * @author dugq
 * @date 2021/8/11 6:50 下午
 */
public class YapiConfigService extends AbstractSingleValueConfigService<YapiConfigBean> {

    public static final String yapiConfigFile = "/yapi.txt";

    public YapiConfigService(Project project) {
        super(project,yapiConfigFile,YapiConfigBean.class);
    }

}
