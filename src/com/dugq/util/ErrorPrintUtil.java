package com.dugq.util;

import com.dugq.component.ErrorTextToolPanel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;

/**
 * Created by dugq on 2021/4/6.
 */
public class ErrorPrintUtil extends BasePrintUtil{

    public static ErrorTextToolPanel getErrorTextToolPanel(Project project){
        Content ams = getContentManager(project).findContent("ERROR Print");
        return (ErrorTextToolPanel)ams.getComponent();
    }


    public static void printLine(String message, Project project) {
        ErrorTextToolPanel errorTextToolPanel = getErrorTextToolPanel(project);
        errorTextToolPanel.appendLine(message);

        getKjjToolWindow(project).show(() ->
                        getContentManager(project).setSelectedContent(
                                        getContentManager(project).findContent("ERROR Print")));
    }

    public static void clear(Project project){
        getErrorTextToolPanel(project).clear();
    }

    public static void printException(Exception e, Project project) {
        printLine(e.getMessage(),project);
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            printLine("\tat   "+stackTraceElement.getClassName()+"#"+stackTraceElement.getMethodName()+"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")",project);
        }
    }
}
