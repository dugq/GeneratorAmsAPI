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
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2019/12/16.
 */
public class YapiUploadApiAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        try {
            APIPrintUtil.clear(project);
            Editor editor = event.getData(PlatformDataKeys.EDITOR);
            if(Objects.isNull(editor)){
                return;
            }
            TargetBean targetBean = TargetUtils.getTargetBean(editor, project);
            List<ApiBean> apiList = getApiUploadBeans(project, targetBean);
            if (CollectionUtils.isNotEmpty(apiList)){
                doUpload(apiList,project);
            }
            Messages.showInfoMessage(project,"上传成功","yapi");
        }catch (StopException e){
            //skip
        }catch (ErrorException e){
            e.printStackTrace();
            APIPrintUtil.printErrorLine(e.getFullMessage(),project);
        }catch (Exception e){
            APIPrintUtil.printErrorLine(e.toString(),project);
            e.printStackTrace();
        }
    }

    private void doUpload(List<ApiBean> apiList,Project project) {
        final YapiInterfaceService apiService = project.getService(YapiInterfaceService.class);
        apiList.forEach(api->{
            final String yapiUrl = apiService.upload(api);
            APIPrintUtil.getAmsToolPanel(project).appendLine("YAPI接口地址", Color.GREEN).append(yapiUrl,Color.BLUE);
        });
    }

    @NotNull
    private List<ApiBean> getApiUploadBeans(Project project, TargetBean targetBean) {
        List<ApiBean> apiList = new ArrayList<>();
        if(Objects.isNull(targetBean.getContainingMethod())){
            PsiMethod[] allMethods = targetBean.getContainingClass().getMethods();
            for (PsiMethod method : allMethods) {
                final ApiBean apiUploadBean = getAndUploadApi(project,targetBean.getContainingClass() , method);
                if (Objects.nonNull(apiUploadBean)){
                    apiList.add(apiUploadBean);
                }
            }
        }else{
            final ApiBean apiUploadBean = getAndUploadApi(project, targetBean.getContainingClass(), targetBean.getContainingMethod());
            if (Objects.nonNull(apiUploadBean)){
                apiList.add(apiUploadBean);
            }
        }
        return apiList;
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
