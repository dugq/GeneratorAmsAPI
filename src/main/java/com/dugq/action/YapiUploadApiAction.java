package com.dugq.action;

import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.TargetBean;
import com.dugq.pojo.ThreadStack;
import com.dugq.service.yapi.YapiInterfaceService;
import com.dugq.util.APIPrintUtil;
import com.dugq.util.ApiUtils;
import com.dugq.util.PsiAnnotationSearchUtil;
import com.dugq.util.SpringMVCConstant;
import com.dugq.util.TargetUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2019/12/16.
 */
public class YapiUploadApiAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if(Objects.isNull(editor)){
            return;
        }
        List<TargetBean> targetBeanList = TargetUtils.getTargetBean2(editor, project);
        for (TargetBean targetBean : targetBeanList) {
            updateOneApi(project, targetBean);
        }
    }

    public void updateOneApi(Project project, TargetBean targetBean) {
        try {
            ApiBean apiList = getApiUploadBeans(project, targetBean);
            doUpload(apiList, project);
            APIPrintUtil.show(project);
        }catch (StopException e){
            //skip
        }catch (ErrorException e){
            e.printStackTrace();
            APIPrintUtil.printErrorLine(e.getFullMessage(), project);
        }catch (Exception e){
            APIPrintUtil.printErrorLine(e.toString(), project);
            e.printStackTrace();
        }
    }

    private void doUpload(ApiBean api,Project project) {
        final YapiInterfaceService apiService = project.getService(YapiInterfaceService.class);
        final String yapiUrl = apiService.upload(api);
        APIPrintUtil.getAmsToolPanel(project).appendWarnLine(api.getApiName()).appendLine(yapiUrl);
    }

    @NotNull
    private ApiBean getApiUploadBeans(Project project, TargetBean targetBean) {
        return getAndUploadApi(project, targetBean.getContainingClass(), targetBean.getContainingMethod());
    }

    private ApiBean getAndUploadApi(Project project, PsiClass containingClass, PsiMethod method) {
        try {
            ThreadStack.init(method);
            PsiAnnotation get = PsiAnnotationSearchUtil.findAnnotation(method, SpringMVCConstant.GetMapping);
            PsiAnnotation post = PsiAnnotationSearchUtil.findAnnotation(method, SpringMVCConstant.PostMapping);
            if (Objects.isNull(get) && Objects.isNull(post)){
                if (Objects.nonNull(PsiAnnotationSearchUtil.findAnnotation(method, SpringMVCConstant.RequestMapping))){
                    throw new ErrorException("请用GetMapping/PostMapping代替RequestMapping");
                }
                return null;
            }
            return ApiUtils.getApiParam(project, method,containingClass);
        }catch (StopException e){
            //skip
        }catch (ErrorException e){
            e.printStackTrace();
            APIPrintUtil.printErrorLine(e.getFullMessage(),project);
        }catch (Exception e){
            APIPrintUtil.printErrorLine(e.toString(),project);
            e.printStackTrace();
        }finally {
            ThreadStack.rest();
        }
        return null;
    }



    @Override
    public boolean isDumbAware() {
        return false;
    }
}
