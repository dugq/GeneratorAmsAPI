package com.dugq.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;

/**
 * Created by dugq on 2021/4/8.
 */
public abstract class BasePrintUtil {

    protected static ToolWindow getKjjToolWindow(Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow("KJJ");
    }

    protected static ContentManager getContentManager(Project project) {
        return getKjjToolWindow(project).getContentManager();
    }

}
