package com.dugq.action;

import com.dugq.bean.ProjectApiBean;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.ams.ApiDetail;
import com.dugq.pojo.ams.RequestParam;
import com.dugq.pojo.ams.SimpleApiVo;
import com.dugq.pojo.enums.ParamTypeEnum;
import com.dugq.pojo.enums.RequestType;
import com.dugq.service.ams.ApiEditorService;
import com.dugq.service.ams.ApiService;
import com.dugq.service.yapi.YapiInterfaceService;
import com.dugq.service.yapi.YapiMenuService;
import com.dugq.service.yapi.YapiProjectService;
import com.dugq.util.APIPrintUtil;
import com.dugq.util.SpringHelper;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/8/15 10:44 下午
 */
public class Ams2YapiAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getProject();
        final YapiInterfaceService apiService = project.getService(YapiInterfaceService.class);
        final YapiMenuService menuService = project.getService(YapiMenuService.class);
        final YapiProjectService projectService = project.getService(YapiProjectService.class);
        final long currentProjectId = projectService.getCurrentProjectId();
        final Map<String, SimpleApiVo> stringSimpleApiVoMap = ApiService.apiList(project);
        final List<ProjectApiBean> allRequest = SpringHelper.getAllRequest(project);
        allRequest.stream().collect(Collectors.toMap(ProjectApiBean::getPath, Function.identity(),(left,right)->{
            APIPrintUtil.printWarnLine("url = "+left.getPath()+"重复了！",project);
            return right;
        }));

        String testApi = "/kjy/mp/activity/saveConfCoupon";
//        movingOneApi(project, apiService, menuService, currentProjectId, projectApiBeanMap.get(testApi), stringSimpleApiVoMap.get(testApi));
        doMoving(project, apiService, menuService, currentProjectId, stringSimpleApiVoMap, allRequest);
    }

    private void doMoving(Project project, YapiInterfaceService apiService, YapiMenuService menuService, long currentProjectId, Map<String, SimpleApiVo> stringSimpleApiVoMap, List<ProjectApiBean> allRequest) {
        int cout = 0;
        for (ProjectApiBean projectApiBean : allRequest) {
            if (projectApiBean.getPsiElement().isDeprecated()){
                continue;
            }
            final String path = projectApiBean.getPath();
//            if (Objects.nonNull(apiService.searchByPath(path, currentProjectId))){
//                continue;
//            }
            final SimpleApiVo simpleApiVo = stringSimpleApiVoMap.get(path);
            if (Objects.isNull(simpleApiVo)){
                APIPrintUtil.printWarnLine("path="+path+"缺少AMS接口", project);
                continue;
            }
            try {
                movingOneApi(project, apiService, menuService, currentProjectId, projectApiBean, simpleApiVo);
                cout++;
            }catch (Exception e){
                System.out.println(path+"失败");
                e.printStackTrace();
            }
        }
        APIPrintUtil.printWarnLine("本次迁移完成"+cout+"个接口",project);
    }

    private void movingOneApi(Project project, YapiInterfaceService apiService, YapiMenuService menuService, long currentProjectId, ProjectApiBean projectApiBean, SimpleApiVo simpleApiVo) {
        final String groupName = simpleApiVo.getGroupName();
        Long menuId = menuService.getOrCreateMenu(currentProjectId,groupName);
        ApiBean apiBean = changeAmsApi2ApiBean(simpleApiVo, projectApiBean, project);
        apiBean.setMenuId(menuId);
        apiService.upload(apiBean,true);
    }

    private ApiBean changeAmsApi2ApiBean(SimpleApiVo simpleApiVo, ProjectApiBean projectApiBean,Project project) {
        final ApiDetail apiDetail = ApiEditorService.getApiDetail(simpleApiVo.getApiID());
        final List<RequestParam> requestInfo = apiDetail.getRequestInfo();
        ApiBean apiBean = new ApiBean();
        List<ParamBean> apiParamBean = apiParamBean(requestInfo);
        if (simpleApiVo.getApiRequestType()==RequestType.post.getType()){
            ParamBean paramBean = new ParamBean();
            paramBean.setParamType(ParamTypeEnum.OBJECT);
            paramBean.setChildren(apiParamBean);
            apiParamBean = Collections.singletonList(paramBean);
        }
        apiBean.setApiParamBean(apiParamBean);
        apiBean.setApiResultParam(apiParamBean(apiDetail.getResultInfo()));
        apiBean.setApiName(simpleApiVo.getApiName());
        apiBean.setApiRequestType(simpleApiVo.getApiRequestType()==RequestType.post.getType()? RequestType.post:RequestType.get);
        apiBean.setApiURI(projectApiBean.getPath());
        apiBean.setDesc(apiDetail.getBaseInfo().getApiNote());
        return apiBean;
    }

    List<ParamBean> apiParamBean(List<RequestParam> paramList){
        final Map<String, List<RequestParam>> paramMap = paramList.stream().collect(Collectors.groupingBy(param -> {
            final String paramKey = param.getParamKey();
            if (paramKey.contains(">>")) {
                return paramKey.substring(0,paramKey.lastIndexOf(">>"));
            }
            return "root";
        }));
        return buildParamList(paramMap,"root");
    }

    @Nullable
    private List<ParamBean> buildParamList(Map<String, List<RequestParam>> paramMap, String key) {
        final List<RequestParam> root = paramMap.get(key);
        if (Objects.isNull(root)){
            return null;
        }
        List<ParamBean> paramBeans = new ArrayList<>();
        for (RequestParam requestParam : root) {
            final Integer paramType = requestParam.getParamType();
            final ParamTypeEnum paramTypeEnum = ParamTypeEnum.getByCode(paramType);
            ParamBean paramBean = new ParamBean();
            String paramKey = requestParam.getParamKey();
            if (paramKey.contains(">>")){
                paramKey = paramKey.substring(paramKey.lastIndexOf(">>")+2);
            }
            paramBean.setParamKey(paramKey);
            paramBean.setParamValue(requestParam.getParamValue());
            paramBean.setParamType(paramTypeEnum);
            paramBean.setParamName(requestParam.getParamName());
            paramBean.setParamNotNull(requestParam.getParamNotNull());
            if (!paramTypeEnum.isNormalType()){
                final List<ParamBean> children = buildParamList(paramMap,requestParam.getParamKey());
                paramBean.setChildren(children);
                if (Objects.equals(paramTypeEnum,ParamTypeEnum.ARRAY)){
                    if (CollectionUtils.isEmpty(children)){
                        paramBean.setChildType(ParamTypeEnum.STRING);
                    }
                    paramBean.setChildType(ParamTypeEnum.OBJECT);
                }
            }
            paramBeans.add(paramBean);
        }
        return paramBeans;
    }

}
