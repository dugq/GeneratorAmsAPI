package com.dugq.action;

import com.dugq.component.testapi.TestApiPanel;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.TargetBean;
import com.dugq.pojo.ThreadStack;
import com.dugq.service.FeignApiService;
import com.dugq.util.ApiUtils;
import com.dugq.util.TargetUtils;
import com.dugq.util.TestApiUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by dugq on 2021/4/7.
 */
public class TestApiAction  extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        TestApiPanel testApiPanel = TestApiUtil.getTestApiPanel(project);
        try {
            final FeignApiService feignApiService = project.getService(FeignApiService.class);
            Editor editor = event.getData(PlatformDataKeys.EDITOR);
            if (Objects.isNull(editor)) {
                return;
            }
            TargetBean targetBean = TargetUtils.getTargetBean(editor, project);
            ThreadStack.init(targetBean.getContainingMethod());
            if (feignApiService.isFeignApi(targetBean.getContainingClass())) {
                final ApiBean feignBean = feignApiService.getFeignBean(targetBean, project);
                testApiPanel.testApi(feignBean);
            } else {
                ThreadStack.init(targetBean.getContainingMethod());
                final ApiBean apiParamBean = ApiUtils.getApiParam(project, targetBean.getContainingMethod(), targetBean.getContainingClass());
                testApiPanel.testApi(apiParamBean);
            }
            testApiPanel.clearResponse();
            TestApiUtil.show(project);
        } catch (ErrorException e){
            testApiPanel.createNewEmptyMainPanelAndSelect();
            TestApiUtil.printErrorLine(e.getFullMessage(),project);
            e.printStackTrace();
        }catch (Exception e){
            testApiPanel.createNewEmptyMainPanelAndSelect();
            TestApiUtil.printException(e,project);
            e.printStackTrace();
        }finally {
            ThreadStack.rest();
        }
    }


}
