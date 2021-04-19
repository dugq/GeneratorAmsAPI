package com.dugq.component;

import com.dugq.service.SaveTestAPIGlobalService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

/**
 * Created by dugq on 2021/3/22.
 */
public class WindowFactoryComponent implements ToolWindowFactory{
    public static final String id = "KJJ";


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        AmsToolPanel dependencyViewer = new AmsToolPanel(project, toolWindow);
        ErrorTextToolPanel errorTextToolPanel = new ErrorTextToolPanel(project, toolWindow);
        TestApiPanel testApiPanel = new TestApiPanel(project, toolWindow);
        Content api = ContentFactory.SERVICE.getInstance().createContent(dependencyViewer, "API Print", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(api,0);
        contentManager.addContent(ContentFactory.SERVICE.getInstance().createContent(errorTextToolPanel, "ERROR Print", false),1);
        Content test_api = ContentFactory.SERVICE.getInstance().createContent(testApiPanel, "TEST_API", false);
        contentManager.addContent(test_api,2);
        SaveTestAPIGlobalService.init(project,testApiPanel);
    }

    @Override
    public void init(ToolWindow toolWindow) {

    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }

}
