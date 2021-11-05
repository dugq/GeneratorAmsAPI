package com.dugq.service.config.impl;

import com.intellij.openapi.project.Project;

/**
 * @author dugq
 * @date 2021/7/8 11:52 下午
 */
public class GlobalParamConfigService extends KeyValueConfigService {
    private static final String GLOBAL_PARAM_FILE = "/globalParams.txt";

    public GlobalParamConfigService(Project project) {
        super(project, GLOBAL_PARAM_FILE);
    }

    public static GlobalParamConfigService getInstance(Project project) {
        return project.getService(GlobalParamConfigService.class);
    }

}
