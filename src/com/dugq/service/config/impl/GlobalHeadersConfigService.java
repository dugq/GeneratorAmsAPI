package com.dugq.service.config.impl;

import com.intellij.openapi.project.Project;

/**
 * @author dugq
 * @date 2021/7/8 11:52 下午
 */
public class GlobalHeadersConfigService extends KeyValueConfigService {
    private static final String GLOBAL_HEADER_FILE = "/globalHeaders.txt";

    public GlobalHeadersConfigService(Project project) {
        super(project,GLOBAL_HEADER_FILE);
    }

    public static GlobalHeadersConfigService getInstance(Project project) {
        return project.getService(GlobalHeadersConfigService.class);
    }
}
