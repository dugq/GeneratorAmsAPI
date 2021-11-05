package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dugq.bean.ResponseBean;
import com.dugq.component.common.CenterSelectDialog;
import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.yapi.GroupBean;
import com.dugq.pojo.yapi.ResultBean;
import com.dugq.pojo.yapi.YapiConfigBean;
import com.dugq.service.config.impl.YapiConfigService;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author dugq
 * @date 2021/8/11 8:03 下午
 */
public class YapiGroupService extends YapiBaseService{

    private final Project project;
    private final YapiConfigService configService;

    public YapiGroupService(Project project) {
        this.project = project;
        configService = project.getService(YapiConfigService.class);
    }

    @Override
    Project getProject() {
        return project;
    }

    public long getCurrentGroup(){
        YapiConfigBean yapiConfigBean = configService.read();
        if (Objects.nonNull(yapiConfigBean)){
            final Long currentGroup = yapiConfigBean.getCurrentGroup();
            if (Objects.nonNull(currentGroup)){
                return currentGroup;
            }
        }
        final List<GroupBean> groupList = getGroupList();
        if (CollectionUtils.isEmpty(groupList)){
            throw new ErrorException("您没有任何空间权限");
        }
        CenterSelectDialog<GroupBean> centerSelectDialog = CenterSelectDialog.getInstance("请选择项目所在空间",groupList,GroupBean::getGroupName,null);
        final boolean b = centerSelectDialog.showAndGet();
        if (!b){
            throw new StopException();
        }
        final String id = centerSelectDialog.getLastSelect().getId();
        if (!NumberUtils.isCreatable(id)){
            throw new ErrorException("空间ID非数字类型。有情况～如何是好呢？");
        }
        final long selectedGroupId = Long.parseLong(id);
        if (Objects.isNull(yapiConfigBean)){
            yapiConfigBean = configService.read();
        }
        yapiConfigBean.setCurrentGroup(selectedGroupId);
        configService.save(yapiConfigBean);
        return selectedGroupId;
    }



    public List<GroupBean> getGroupList(){
        try {
            ResponseBean responseBean = sendGet(UrlFactory.groupUrl,null);
            if (!responseBean.isSuccess()){
                throw new ErrorException("获取空间列表失败 response status="+responseBean.getStatus());
            }
            final String responseBody = responseBean.getResponseBody();
            ResultBean<List<GroupBean>> result = JSON.parseObject(responseBody, new TypeReference<ResultBean<List<GroupBean>>>(){});
            if (!result.isSuccess()){
                throw new ErrorException("获取空间列表失败  errorcode="+result.getErrcode()+"  errmsg="+result.getErrmsg());
            }
            return result.getData();
        } catch (IOException e) {
            throw new ErrorException("获取空间列表失败");
        }
    }
}
