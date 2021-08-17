package com.dugq.util;

import com.dugq.component.tool.WindowFactoryComponent;
import com.dugq.component.testapi.TestApiPanel;
import com.dugq.exception.ErrorException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;

/**
 * Created by dugq on 2021/4/7.
 */
public class TestApiUtil extends BasePrintUtil{

    public static TestApiPanel getTestApiPanel(Project project){
        Content ams =getContentManager(project).findContent(WindowFactoryComponent.TEST_PANEL_NAME);
        return (TestApiPanel)ams.getComponent();
    }

    public static void show(Project project){
        getKjjToolWindow(project).show(()->
                        getContentManager(project).setSelectedContent(
                                        getContentManager(project).findContent(WindowFactoryComponent.TEST_PANEL_NAME)));
    }

    public static void printException(Exception e, Project project) {
        if (e instanceof ErrorException){
            printErrorLine(((ErrorException) e).getFullMessage(),project);
            return;
        }
        final TestApiPanel testApiPanel = getTestApiPanel(project);
        testApiPanel.clearResponse();
        StringBuilder message = new StringBuilder();
        StackTraceElement[] stackTrace = e.getStackTrace();
        message.append(e.getMessage());
        message.append("\n");
        for (StackTraceElement stackTraceElement : stackTrace) {
            message.append("\tat   "+stackTraceElement.getClassName()+"#"+stackTraceElement.getMethodName()+"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")\n");
        }
        testApiPanel.printError(message.toString());
        show(project);
    }


    public static void printErrorLine(String fullMessage, Project project) {
        final TestApiPanel testApiPanel = getTestApiPanel(project);
        testApiPanel.clearResponse();
        testApiPanel.printError(fullMessage);
        show(project);
    }

}
