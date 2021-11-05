package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dugq.bean.ResponseBean;
import com.dugq.component.common.CenterSelectDialog;
import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.yapi.ListBean;
import com.dugq.pojo.yapi.ResultBean;
import com.dugq.pojo.yapi.YapiConfigBean;
import com.dugq.pojo.yapi.YapiProjectBean;
import com.dugq.service.config.impl.YapiConfigService;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dugq
 * @date 2021/8/11 8:38 下午
 */
public class YapiProjectService extends YapiBaseService{
    private final Project project;
    private final YapiConfigService configService;
    private final YapiGroupService yapiGroupService;

    public YapiProjectService(Project project) {
        this.project = project;
        this.configService = project.getService(YapiConfigService.class);
        yapiGroupService = project.getService(YapiGroupService.class);
    }

    @Override
    Project getProject() {
        return project;
    }


    public long getCurrentProjectId(){
        YapiConfigBean yapiConfigBean = configService.read();
        if (Objects.nonNull(yapiConfigBean)){
            final Long currentProjectId = yapiConfigBean.getCurrentProject();
            if (Objects.nonNull(currentProjectId)){
                return currentProjectId;
            }
        }
        final long currentGroup = yapiGroupService.getCurrentGroup();
        final List<YapiProjectBean> projectList = getProjectList(currentGroup);
        if (CollectionUtils.isEmpty(projectList)){
            throw new ErrorException("您没有任何项目权限");
        }
        CenterSelectDialog<YapiProjectBean> centerSelectDialog = CenterSelectDialog.getInstance("请选择项目",projectList,YapiProjectBean::getName,null);
        final boolean b = centerSelectDialog.showAndGet();
        if (!b){
            throw new StopException();
        }
        final Long selectedProjectId = centerSelectDialog.getLastSelect().getId();
        if (Objects.isNull(yapiConfigBean)){
            yapiConfigBean = configService.read();
        }
        yapiConfigBean.setCurrentProject(selectedProjectId);
        configService.save(yapiConfigBean);
        return selectedProjectId;
    }

    public List<YapiProjectBean> getProjectList(long groupId){
        Map<String,String> params = new HashMap<>();
        params.put("group_id",String.valueOf(groupId));
        params.put("page","1");
        params.put("limit","100");
        try {
            final ResponseBean responseBean = sendGet(UrlFactory.projectUrl, params);
            final String responseBody = responseBean.getResponseBody();
            ResultBean<ListBean<YapiProjectBean>> result = JSON.parseObject(responseBody, new TypeReference<ResultBean<ListBean<YapiProjectBean>>>(){});
            if (!result.isSuccess()){
                throw new ErrorException("获取项目列表失败 errorcode="+result.getErrcode()+"errmsg="+result.getErrmsg());
            }
            return result.getData().getList();
        } catch (IOException e) {
            throw new ErrorException("获取项目列表失败");
        }
    }

}
