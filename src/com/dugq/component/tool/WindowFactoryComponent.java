package com.dugq.component.tool;

import com.dugq.component.ams.AmsToolPanel;
import com.dugq.component.testapi.TestApiPanel;
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
    public static final String API_PANEL_NAME = "API Print";
    public static final String TEST_PANEL_NAME = "TEST_API";
    public static final String ERROR_PANEL_NAME = "ERROR Print";


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        AmsToolPanel dependencyViewer = new AmsToolPanel(project, toolWindow);
        ErrorTextToolPanel errorTextToolPanel = new ErrorTextToolPanel(project, toolWindow);
        TestApiPanel testApiPanel = new TestApiPanel(project, toolWindow);
        Content api = ContentFactory.SERVICE.getInstance().createContent(dependencyViewer, API_PANEL_NAME, false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(api,0);
        contentManager.addContent(ContentFactory.SERVICE.getInstance().createContent(errorTextToolPanel, ERROR_PANEL_NAME, false),1);
        Content test_api = ContentFactory.SERVICE.getInstance().createContent(testApiPanel, TEST_PANEL_NAME, false);
        contentManager.addContent(test_api,2);
    }

    @Override
    public void init(ToolWindow toolWindow) {

    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }

}
