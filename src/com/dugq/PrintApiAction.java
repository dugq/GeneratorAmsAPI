package com.dugq;

import com.dugq.component.WindowComponent;
import com.dugq.pojo.EditorParam;
import com.dugq.pojo.TargetBean;
import com.dugq.util.ApiUtils;
import com.dugq.util.PrintUtil;
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
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        TargetBean targetBean = TargetUtils.getTargetBean(editor, project);
        if (Objects.isNull(targetBean.getContainingMethod())){
            WindowComponent.printLine("请选择方法！");
            return;
        }
        EditorParam apiParam;
        try {
            apiParam = ApiUtils.getApiParam(project, targetBean.getContainingMethod(),targetBean.getContainingClass());
        }catch (Exception e){
            WindowComponent.printLine(e.getMessage());
            e.printStackTrace();
            return;
        }
        PrintUtil.print(apiParam);

    }
}
