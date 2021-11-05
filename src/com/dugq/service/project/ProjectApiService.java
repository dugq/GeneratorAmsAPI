package com.dugq.service.project;

import com.dugq.bean.ProjectApiBean;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dugq
 * @date 2021/8/15 11:45 下午
 */
public class ProjectApiService {

    private final Project project;
    Map<String,ProjectApiBean> api_cache = new HashMap<>();

    public ProjectApiService(Project project) {
        this.project = project;
    }

    public List<ProjectApiBean> getAllApiList(){
        if (MapUtils.isEmpty(api_cache)){
            refreshApi();
        }
        return new ArrayList<>(api_cache.values());
    }

    public void refreshApi(){
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            List<ProjectApiBean> apiList = getModuleRequests(project, module);
            if (CollectionUtils.isEmpty(apiList)){
                continue;
            }
            apiList.forEach(api->api_cache.put(api.getPath(),api));
        }
    }

    private List<ProjectApiBean> getModuleRequests(Project project, Module module) {
        return null;
    }
}
