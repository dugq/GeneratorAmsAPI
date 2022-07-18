package com.dugq.service.config.impl;

import com.intellij.openapi.project.Project;

/**
 * @author dugq
 * @date 2021/7/8 11:52 下午
 */
public class GlobalHostConfigService extends KeyValueConfigService {
    private static final String HOST_SETTING_FILE = "/globalHost.txt";


    public GlobalHostConfigService(Project project) {
        super(project,HOST_SETTING_FILE);
    }

    public static GlobalHostConfigService getInstance(Project project) {
        return project.getService(GlobalHostConfigService.class);
    }

}
