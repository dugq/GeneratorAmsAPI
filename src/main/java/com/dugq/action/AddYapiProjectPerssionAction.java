package com.dugq.action;

import com.dugq.exception.ErrorException;
import com.dugq.service.yapi.YapiAdminService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * @author dugq
 * @date 2021/11/11 8:53 下午
 */
public class AddYapiProjectPerssionAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getProject();
        try {
            YapiAdminService adminService = project.getService(YapiAdminService.class);
            if (adminService.addCurrentProjectDeveloperPermission()){
                Messages.showInfoMessage(project,"权限已添加","申请权限");
            }else{
                Messages.showErrorDialog(project,"权限添加失败","申请权限");
            }
        }catch (ErrorException e){
            Messages.showErrorDialog(project,e.getMessage(),"申请权限");
        }
    }
}
