package com.dugq.component.ams;

import com.dugq.pojo.ParamBean;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dugq on 2021/4/6.
 */
public class AmsToolPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final ToolWindow toolWindow;
    private ConsoleViewImpl information;

    public AmsToolPanel(Project p, ToolWindow t) {
        super(true, true);
        this.project = p;
        this.toolWindow = t;
        information = (ConsoleViewImpl)TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        final JComponent component = information.getComponent();
        setContent(component);
    }

    public AmsToolPanel append(String msg){
        information.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);
        return this;
    }

    public AmsToolPanel append(String msg,ConsoleViewContentType color){
        information.print(msg, color);
        return this;
    }

    public AmsToolPanel appendLine(String msg){
        append(msg+"\n");
        return this;
    }

    public AmsToolPanel enterLine(){
        information.print("\n",ConsoleViewContentType.NORMAL_OUTPUT);
        return this;
    }

    public AmsToolPanel appendLine(String msg , ConsoleViewContentType color){
        information.print(msg+"\n",color);
        return this;
    }

    public AmsToolPanel appendErrorLine(String msg){
        appendLine(msg, ConsoleViewContentType.ERROR_OUTPUT);
        return this;
    }

    public AmsToolPanel appendWarnLine(String msg){
        appendLine(msg, ConsoleViewContentType.LOG_WARNING_OUTPUT);
        return this;
    }

    public AmsToolPanel appendInfoLine(String msg){
        appendLine(msg, ConsoleViewContentType.NORMAL_OUTPUT);
        return this;
    }

    public AmsToolPanel append(List<ParamBean> apiParamBean) {
        doPrintParamBean(apiParamBean,0);
        return this;
    }

    private void doPrintParamBean(List<ParamBean> apiParamBean,int blankLength) {
        if (CollectionUtils.isEmpty(apiParamBean)){
            return;
        }
        final Integer max = apiParamBean.stream().map(bean -> bean.getParamKey().length()).max(Comparator.comparing(in -> in)).get();
        for (ParamBean paramBean : apiParamBean) {
            append(getFrontBlank(blankLength))
            .append(paramBean.getParamKey())
            .append(getBlank(max+3-paramBean.getParamKey().length())+":  ")
            .append(paramBean.getParamType().getName())
            .append(getBlank(8-paramBean.getParamType().getName().length())+";  ");
            if (StringUtils.isBlank(paramBean.getParamName())){
                appendLine("自行填充字段描述",ConsoleViewContentType.ERROR_OUTPUT);
            }else{
                appendLine(paramBean.getParamName());
            }
            final List<ParamBean> children = paramBean.getChildren();
            doPrintParamBean(children,blankLength+1);
        }
    }

    public String getFrontBlank(int num){
        if(num==0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i =0 ; i<num*4; i++){
            if (i%4==0){
                sb.append("├");
            }else{
                sb.append("-");
            }
        }
        return sb.toString();
    }

    public String getBlank(int num){
        if(num<=0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i =0 ; i<num; i++){
            sb.append(" ");
        }
        return sb.toString();
    }

    public void clear() {
        information.clear();
    }
}
