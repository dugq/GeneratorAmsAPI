package com.dugq.util;

import com.dugq.component.tool.ErrorTextToolPanel;
import com.dugq.exception.ErrorException;
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


    public static void printErrorLine(String message, Project project) {
        ErrorTextToolPanel errorTextToolPanel = getErrorTextToolPanel(project);
        errorTextToolPanel.appendErrorLine(message);

        getKjjToolWindow(project).show(() ->
                        getContentManager(project).setSelectedContent(
                                        getContentManager(project).findContent("ERROR Print")));
    }

    public static void printWarnLine(String message, Project project) {
        ErrorTextToolPanel errorTextToolPanel = getErrorTextToolPanel(project);
        errorTextToolPanel.appendWarnLine(message);

        getKjjToolWindow(project).show(() ->
                getContentManager(project).setSelectedContent(
                        getContentManager(project).findContent("ERROR Print")));
    }

    public static void clear(Project project){
        getErrorTextToolPanel(project).clear();
    }

    public static void printException(Exception e, Project project) {
        printErrorLine(e.getMessage(),project);
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            printErrorLine("\tat   "+stackTraceElement.getClassName()+"#"+stackTraceElement.getMethodName()+"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")",project);
        }
    }

    public static void printError(ErrorException e, Project project) {
        printErrorLine(e.getFullMessage(),project);
    }
}
