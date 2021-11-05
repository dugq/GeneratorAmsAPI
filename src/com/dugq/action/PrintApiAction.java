package com.dugq.action;

import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.TargetBean;
import com.dugq.pojo.ThreadStack;
import com.dugq.util.APIPrintUtil;
import com.dugq.util.ApiUtils;
import com.dugq.util.TargetUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by dugq on 2021/3/23.
 */
public class PrintApiAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        APIPrintUtil.clear(project);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        TargetBean targetBean = TargetUtils.getTargetBean(editor, project);
        if (Objects.isNull(targetBean.getContainingMethod())){
            APIPrintUtil.printErrorLine("请选择request handler mapping！",project);
            return;
        }
        try {
            ThreadStack.init(targetBean.getContainingMethod());
            final ApiBean apiParamBean = ApiUtils.getApiParam(project, targetBean.getContainingMethod(), targetBean.getContainingClass());
            APIPrintUtil.print(apiParamBean,project);
        }catch (StopException e){
            e.printStackTrace();
        } catch (ErrorException e){
            APIPrintUtil.printErrorLine(e.getFullMessage(),project);
            e.printStackTrace();
        }catch (Exception e){
            APIPrintUtil.printException(e,project);
            e.printStackTrace();
        }finally {
            ThreadStack.rest();
        }
    }
}
