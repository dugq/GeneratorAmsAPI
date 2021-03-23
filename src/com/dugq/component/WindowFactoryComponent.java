package com.dugq.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by dugq on 2021/3/22.
 */
public class WindowFactoryComponent implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        WindowComponent.init(toolWindow);
        WindowComponent.printLine("HELLO ~ CLEVER CODER!");
        toolWindow.hide(null);
    }

}
