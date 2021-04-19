package com.dugq.action;

import com.alibaba.fastjson.JSONObject;
import com.dugq.component.TestApiPanel;
import com.dugq.pojo.EditorParam;
import com.dugq.pojo.TargetBean;
import com.dugq.pojo.enums.RequestType;
import com.dugq.requestmapping.mapping.bean.RequestMapping;
import com.dugq.requestmapping.param.RPCParamBuildUtil;
import com.dugq.requestmapping.param.bean.ParamBean;
import com.dugq.service.TestApiService;
import com.dugq.util.ApiUtils;
import com.dugq.util.ErrorPrintUtil;
import com.dugq.util.Param2PrintJSON;
import com.dugq.util.SpringMVCConstant;
import com.dugq.util.TargetUtils;
import com.dugq.util.TestApiUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2021/4/7.
 */
public class TestApiAction  extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if(Objects.isNull(editor)){
            return;
        }
        TargetBean targetBean = TargetUtils.getTargetBean(editor, project);
        if (targetBean.getContainingClass().hasAnnotation(SpringMVCConstant.AdvancedFeign)){
            RequestMapping feignMapping = getFeign(targetBean);
            TestApiUtil.show(project);
            JSONObject request = new JSONObject();
            TestApiPanel testApiPanel = TestApiUtil.getTestApiPanel(project);
            TestApiService testApiService = TestApiService.getInstance(project);
            testApiPanel.clearAndSetParam(testApiService.dealRequestParam(request));
            testApiPanel.setUri(feignMapping.getUri());
            testApiPanel.setRequestMethod( RequestType.getDescByType(feignMapping.getReqType()));
        }else{
            EditorParam param = getRestApiEditorParam(project, targetBean);
            if (param == null) return;
            TestApiUtil.show(project);
            JSONObject request = Param2PrintJSON.param2Json(param.getApiRequestParam());
            TestApiPanel testApiPanel = TestApiUtil.getTestApiPanel(project);
            TestApiService testApiService = TestApiService.getInstance(project);
            testApiPanel.clearAndSetParam(testApiService.dealRequestParam(request));
            testApiPanel.setUri(param.getApiURI());
            testApiPanel.setRequestMethod( RequestType.getDescByType(param.getApiRequestType()));
        }
    }

    private RequestMapping getFeign(TargetBean targetBean) {
        RequestMapping editorParam = new RequestMapping();
        String className = targetBean.getContainingClass().getName();
        String methodName = targetBean.getContainingMethod().getName();
        if (Objects.isNull(className)){
            return null;
        }
        editorParam.setUri("/"+lowerFirst(className)+"/"+methodName);
        editorParam.setReqType(RequestType.post.getType());

        List<ParamBean> list = RPCParamBuildUtil.getList(targetBean.getContainingMethod());
        editorParam.setParamBeanList(list);
        return editorParam;
    }

    public static String lowerFirst(String oldStr){

        char[]chars = oldStr.toCharArray();

        chars[0] += 32;

        return String.valueOf(chars);

    }

    @Nullable
    protected EditorParam getRestApiEditorParam(Project project, TargetBean targetBean) {
        EditorParam param;
        try {
            param = ApiUtils.getApiParam(project, targetBean.getContainingMethod(),targetBean.getContainingClass());
        }catch (Exception e){
            ErrorPrintUtil.printLine(e.getMessage(),project);
            return null;
        }
        return param;
    }
}
