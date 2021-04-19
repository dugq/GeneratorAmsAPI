package com.dugq.util;

import com.dugq.component.TestApiPanel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;

/**
 * Created by dugq on 2021/4/7.
 */
public class TestApiUtil extends BasePrintUtil{

    public static TestApiPanel getTestApiPanel(Project project){
        Content ams =getContentManager(project).findContent(TestApiPanel.id);
        return (TestApiPanel)ams.getComponent();
    }

    public static void show(Project project){
        getKjjToolWindow(project).show(()->
                        getContentManager(project).setSelectedContent(
                                        getContentManager(project).findContent(TestApiPanel.id)));
    }
}
