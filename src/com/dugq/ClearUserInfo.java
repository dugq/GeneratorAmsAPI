package com.dugq;

import com.dugq.ams.LoginService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by dugq on 2019/12/27.
 */
public class ClearUserInfo extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        LoginService.clearUserInfo();
    }
}
