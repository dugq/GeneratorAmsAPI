package com.dugq.util;

import com.dugq.component.tool.WindowFactoryComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;

/**
 * Created by dugq on 2021/4/8.
 */
public abstract class BasePrintUtil {

    protected static ToolWindow getKjjToolWindow(Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow(WindowFactoryComponent.id);
    }

    protected static ContentManager getKjjWindowContentManager(Project project) {
        return getKjjToolWindow(project).getContentManager();
    }

    protected static void showMyWindow(Project project) {
        final ToolWindow kjjToolWindow = getKjjToolWindow(project);
        kjjToolWindow.show();
    }

}
