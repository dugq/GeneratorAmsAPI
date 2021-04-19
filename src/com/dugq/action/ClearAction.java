package com.dugq.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dugq on 2021/4/7.
 */
public class ClearAction extends AnAction {


    public ClearAction() {
        super("clean","清理当前窗口",null);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Component component = anActionEvent.getDataContext().getData(PlatformDataKeys.CONTEXT_COMPONENT);
        if (component instanceof JTextPane){
            ((JTextPane)component).setText("");
        }else if(component instanceof JTextArea){
            ((JTextArea)component).setText("");
        }
    }
}
