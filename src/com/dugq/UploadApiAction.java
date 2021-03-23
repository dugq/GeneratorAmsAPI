package com.dugq;

import com.dugq.ams.ApiEditorService;
import com.dugq.ams.LoginService;
import com.dugq.component.WindowComponent;
import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.EditorParam;
import com.dugq.pojo.GroupVo;
import com.dugq.pojo.TargetBean;
import com.dugq.util.ApiParamBuildUtil;
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

import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2019/12/16.
 */
public class UploadApiAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if(Objects.isNull(editor)){
            return;
        }

        String login = LoginService.login(project);
        Boolean isLogin = LoginService.checkLogin(project,login);

        if(!isLogin){
            ApiParamBuildUtil.error("账号密码错误",project);
            return;
        }
        List<GroupVo> groupVos = ApiEditorService.allGroup(project);

        TargetBean targetBean = TargetUtils.getTargetBean(editor, project);

        if(Objects.isNull(targetBean.getContainingMethod())){
            PsiMethod[] allMethods = targetBean.getContainingClass().getMethods();
            for (PsiMethod method : allMethods) {
                PsiAnnotation get = PsiAnnotationSearchUtil.findAnnotation(method, SpringMVCConstant.GetMapping);
                PsiAnnotation post = PsiAnnotationSearchUtil.findAnnotation(method, SpringMVCConstant.PostMapping);
                if(Objects.nonNull(get) || Objects.nonNull(post)){
                    getAndUploadApi(project, groupVos, targetBean.getContainingClass(), method);
                }
            }
        }else{
            getAndUploadApi(project, groupVos, targetBean.getContainingClass(), targetBean.getContainingMethod());
        }

    }

    private void getAndUploadApi(Project project, List<GroupVo> groupVos, PsiClass containingClass, PsiMethod method) {
        try {
            EditorParam apiParam = ApiUtils.getApiParam(project, method,containingClass);
            ApiEditorService.uploadAPI(project,apiParam,groupVos);
        }catch (StopException e){
            //skip
        }catch (ErrorException e){
            WindowComponent.printLine(e.toString());
        }catch (Exception e){
            WindowComponent.printLine(e.toString());
        }
    }


    @Override
    public boolean isDumbAware() {
        return false;
    }
}
