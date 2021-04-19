package com.dugq.listener;

import com.dugq.component.WindowFactoryComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by dugq on 2021/4/8.
 */
public class InitListener implements ToolWindowManagerListener {
    private final Project project;

    public InitListener(Project project) {
        this.project = project;
    }

    @Override
    public void toolWindowRegistered(@NotNull String id) {

        if (StringUtils.equals(WindowFactoryComponent.id,id)){

        }
    }

    @Override
    public void stateChanged() {
    }
}
