package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dugq.bean.ResponseBean;
import com.dugq.component.yapi.YapiAddMenuComponent;
import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.yapi.ResultBean;
import com.dugq.pojo.yapi.YapiMenuBean;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dugq
 * @date 2021/8/11 9:11 下午
 */
public class YapiMenuService implements YapiBaseService{
    private final Project project;
    public YapiMenuService(Project project) {
        this.project = project;
    }

    public List<YapiMenuBean> getMenuList(long projectId){
        Map<String,String> params = new HashMap<>();
        params.put("project_id",String.valueOf(projectId));
        try {
            final ResponseBean responseBean = sendGet(UrlFactory.listMenu, params);
            final String responseBody = responseBean.getResponseBody();
            ResultBean<List<YapiMenuBean>> result = JSON.parseObject(responseBody, new TypeReference<ResultBean<List<YapiMenuBean>>>(ResultBean.class,List.class,YapiMenuBean.class){});
            if (!result.isSuccess()){
                throw new ErrorException("获取分类列表失败 errorcode="+result.getErrcode()+"errmsg="+result.getErrmsg());
            }
            return result.getData();
        } catch (IOException e) {
            throw new ErrorException("获取分类列表失败");
        }
    }

    @Override
    public Project getProject() {
        return project;
    }

    public long createMenu(long projectId) {
        YapiAddMenuComponent yapiAddMenuComponent = new YapiAddMenuComponent();
        final boolean result = yapiAddMenuComponent.showAndGet();
        if (result){
            return doCreateMenu(projectId, yapiAddMenuComponent.getName(),yapiAddMenuComponent.getDesc());
        }
        throw new StopException();
    }

    private long doCreateMenu(long projectId,String name,String desc) {
        try {
            YapiMenuBean yapiMenuBean = new YapiMenuBean();
            yapiMenuBean.setName(name);
            yapiMenuBean.setDesc(desc);
            yapiMenuBean.setProjectId(projectId);
            final ResponseBean responseBean = sendPost(UrlFactory.addMenu, yapiMenuBean);
            final String responseBody = responseBean.getResponseBody();
            ResultBean<YapiMenuBean> response = JSON.parseObject(responseBody, new TypeReference<ResultBean<YapiMenuBean>>(ResultBean.class,YapiMenuBean.class){});
            if (!response.isSuccess()){
                throw new ErrorException("新增分类失败 errorcode="+response.getErrcode()+"errmsg="+response.getErrmsg());
            }
            return response.getData().getId();
        } catch (IOException e) {
            throw new ErrorException("新建分类失败");
        }
    }

    public Long getOrCreateMenu(long currentProjectId, String menuName) {
        final List<YapiMenuBean> menuList = getMenuList(currentProjectId);
        final YapiMenuBean menu = getMenu(menuList, menuName);
        if (Objects.nonNull(menu)){
            return menu.getId();
        }
        return doCreateMenu(currentProjectId,menuName,"迁移自AMS");
    }

    private YapiMenuBean getMenu(List<YapiMenuBean> menuList,String menuName){
        for (YapiMenuBean yapiMenuBean : menuList) {
            if (StringUtils.equals(yapiMenuBean.getName(),menuName)){
                return yapiMenuBean;
            }
        }
        return null;
    }
}
