package com.dugq.action;

import com.dugq.ams.ApiEditorService;
import com.dugq.ams.LoginService;
import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.EditorParam;
import com.dugq.pojo.GroupVo;
import com.dugq.pojo.SimpleApiVo;
import com.dugq.pojo.TargetBean;
import com.dugq.util.ApiParamBuildUtil;
import com.dugq.util.ApiUtils;
import com.dugq.util.ErrorPrintUtil;
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
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
            EditorParam param = ApiUtils.getApiParam(project, method,containingClass);
            String uri = param.getApiURI();
            List<SimpleApiVo> simpleApiVos = getSimpleApiVos(project, uri);
            if(CollectionUtils.isNotEmpty(simpleApiVos)){
                if(simpleApiVos.size()>1){
                    throw new ErrorException(method,null,"存在多个相同URI的API，无法添加！！！");
                }
                int update = Messages.showDialog("请选择是否更新接口:"+uri, "存在相同uri接口，是否更新？", new String[]{"是", "否"}, 0, null);
                if (update==0){
                    SimpleApiVo simpleApiVo = simpleApiVos.get(0);
                    param.setGroupID(simpleApiVo.getGroupID());
                    param.setApiID(simpleApiVo.getApiID());
                    param.setType(1);
                }else{
                    throw new ErrorException(method,null,"存在同名接口"+uri+"，无法添加！！！");
                }

            }
            ApiEditorService.uploadAPI(project,param,groupVos);
        }catch (StopException e){
            //skip
        }catch (ErrorException e){
            ErrorPrintUtil.printLine(e.toString(),project);
        }catch (Exception e){
            ErrorPrintUtil.printLine(e.toString(),project);
        }
    }

    private static List<SimpleApiVo> getSimpleApiVos(Project project, String uri) {
        List<SimpleApiVo> simpleApiVos = ApiEditorService.amsApiSearchParam(project, uri);
        if (Objects.isNull(simpleApiVos)){
            return Collections.emptyList();
        }
        return simpleApiVos.stream().filter(vo-> StringUtils.equals(vo.getApiURI(),uri)).collect(Collectors.toList());
    }


    @Override
    public boolean isDumbAware() {
        return false;
    }
}
