package com.dugq.action;

import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.TargetBean;
import com.dugq.pojo.ThreadStack;
import com.dugq.pojo.ams.EditorParam;
import com.dugq.pojo.ams.GroupVo;
import com.dugq.pojo.ams.RequestParam;
import com.dugq.pojo.ams.SimpleApiVo;
import com.dugq.service.ams.ApiEditorService;
import com.dugq.service.ams.LoginService;
import com.dugq.util.APIPrintUtil;
import com.dugq.util.ApiUtils;
import com.dugq.util.ParamBeanUtils;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by dugq on 2019/12/16.
 */
public class AmsUploadApiAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        APIPrintUtil.clear(project);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if(Objects.isNull(editor)){
            return;
        }

        LoginService.login(project);
        LoginService.checkLogin(project);

        List<GroupVo> groupVos = ApiEditorService.allGroup();

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
            ThreadStack.init(method);
            ApiBean param = ApiUtils.getApiParam(project, method,containingClass);
            String uri = param.getApiURI();
            final SimpleApiVo updateApi = validatorIfUpdate( uri);
            EditorParam editorParam = changeBean2AmsParam(param,updateApi);
            ApiEditorService.uploadAPI(project,editorParam,groupVos);
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
    }

    private EditorParam changeBean2AmsParam(ApiBean param, SimpleApiVo updateApi) {
        EditorParam editorParam = new EditorParam();
        editorParam.setType(Objects.isNull(updateApi)?2:1);
        editorParam.setApiID(Objects.isNull(updateApi)?null:updateApi.getApiID());
        editorParam.setGroupID(Objects.isNull(updateApi)?null:updateApi.getGroupID());
        editorParam.setApiName(param.getApiName());
        editorParam.setApiRequestType(param.getApiRequestType().getType());
        editorParam.setApiURI(param.getApiURI());
        editorParam.setApiRequestParam(changeParam(param.getApiParamBean(),new AtomicInteger(0),null,true));
        editorParam.setApiResultParam(changeParam(param.getApiResultParam(),new AtomicInteger(0),null,false));
        editorParam.setApiSuccessMock(ParamBeanUtils.param2Json(param.getApiResultParam(),false).toJSONString());
        return editorParam;
    }

    private List<RequestParam> changeParam(List<ParamBean> source, AtomicInteger index , String parentKey,boolean discardParent){
        List<RequestParam> paramList = new ArrayList<>();
        for (ParamBean paramBean : source) {
            String currentFullKey = getAmsKey(parentKey, paramBean.getParamKey(), discardParent);
            if (CollectionUtils.isNotEmpty(paramBean.getChildren())){
                if (discardParent){
                    final List<RequestParam> childrenParamList = changeParam(paramBean.getChildren(), index, "", false);
                    if (CollectionUtils.isNotEmpty(childrenParamList)){
                        paramList.addAll(childrenParamList);
                    }
                    continue;
                }else{
                    final List<RequestParam> childrenParamList = changeParam(paramBean.getChildren(), index, currentFullKey, false);
                    if (CollectionUtils.isNotEmpty(childrenParamList)){
                        paramList.addAll(childrenParamList);
                    }
                }
            }
            RequestParam requestParam = getRequestParam(index, paramBean, currentFullKey);
            paramList.add(requestParam);
        }
        return paramList;
    }

    @NotNull
    private RequestParam getRequestParam(AtomicInteger index, ParamBean paramBean, String currentFullKey) {
        RequestParam requestParam = new RequestParam();
        requestParam.set$index(index.getAndIncrement());
        requestParam.setParamKey(currentFullKey);
        requestParam.setParamName(paramBean.getParamName());
        requestParam.setParamNotNull(paramBean.getParamNotNull());
        requestParam.setParamType(paramBean.getParamType().getType());
        requestParam.setParamValue(paramBean.getParamValue());
        requestParam.setParamValueList(paramBean.getParamValueList());
        return requestParam;
    }

    private String getAmsKey(String parent,String currentKey,boolean discardParent){
        if (StringUtils.isBlank(parent)){
            return currentKey;
        }
        if (discardParent && !parent.contains(">>")){
            return currentKey;
        }
        return parent+">>"+currentKey;
    }

    private SimpleApiVo validatorIfUpdate(String uri) {
        List<SimpleApiVo> simpleApiVos = getSimpleApiVos(uri);
        if(CollectionUtils.isNotEmpty(simpleApiVos)){
            if(simpleApiVos.size()>1){
                throw new ErrorException("接口路径重复");
            }
            final SimpleApiVo simpleApiVo = simpleApiVos.get(0);
            final String apiName = simpleApiVo.getApiName();
            final String groupName = simpleApiVo.getGroupName();
            String detail = "接口描述："+apiName
                    +"\n ams分组："+groupName
                    +"\n url: "+uri;
            int update = Messages.showDialog(detail, "存在相同uri接口，是否更新？", new String[]{"是", "否"}, 0, null);
            if (update==0){
                return simpleApiVo;
            }else{
                throw new StopException();
            }
        }
        return null;
    }

    private static List<SimpleApiVo> getSimpleApiVos( String uri) {
        List<SimpleApiVo> simpleApiVos = ApiEditorService.amsApiSearchParam( uri);
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
