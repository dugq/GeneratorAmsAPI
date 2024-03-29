package com.dugq.util;

import com.dugq.component.ams.AmsToolPanel;
import com.dugq.component.tool.WindowFactoryComponent;
import com.dugq.pojo.ApiBean;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;

import java.util.Objects;

/**
 * Created by dugq on 2021/3/22.
 */
public class APIPrintUtil extends BasePrintUtil{

    public static void printErrorLine(String message, Project project) {
        AmsToolPanel errorTextToolPanel = getAmsToolPanel(project);
        errorTextToolPanel.appendErrorLine(message);
        show(project);
    }

    public static void printException(Exception e, Project project) {
        printErrorLine(e.getMessage(),project);
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            printErrorLine("\tat   "+stackTraceElement.getClassName()+"#"+stackTraceElement.getMethodName()+"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")",project);
        }
        show(project);
    }

    public static void printWarnLine(String message, Project project) {
        AmsToolPanel errorTextToolPanel = getAmsToolPanel(project);
        errorTextToolPanel.appendWarnLine(message);
        show(project);
    }

    public static AmsToolPanel getAmsToolPanel(Project project){
        Content ams = getKjjWindowContentManager(project).findContent(WindowFactoryComponent.API_PANEL_NAME);
        return (AmsToolPanel)ams.getComponent();
    }

    public static void show(Project project){
        final ContentManager contentManager = getKjjWindowContentManager(project);
        final Content content = contentManager.findContent(WindowFactoryComponent.API_PANEL_NAME);
        contentManager.setSelectedContent(content);
        showMyWindow(project);
    }

    public static void print(ApiBean param, Project project) {
        AmsToolPanel amsToolPanel = getAmsToolPanel(project);
        amsToolPanel.append("接口描述:   ")
        .appendLine(param.getApiName())
        .append((Objects.equals(param.getApiRequestType().getType(),0)?"  Post   ":"  Get   "),ConsoleViewContentType.USER_INPUT)
        .appendLine(param.getApiURI(),ConsoleViewContentType.USER_INPUT)
        .appendWarnLine("-------param--------")
        .append(param.getApiParamBean())
        .appendWarnLine("-------result--------")
        .append(param.getApiResultParam());
        show(project);
    }

    public static void clear(Project project) {
        getAmsToolPanel(project).clear();

    }
}
