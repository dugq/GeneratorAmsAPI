package com.dugq.component.ams;

import com.dugq.component.tool.KjjMenu;
import com.dugq.pojo.ParamBean;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ScrollPaneFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.List;

/**
 * Created by dugq on 2021/4/6.
 */
public class AmsToolPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final ToolWindow toolWindow;
    private final Splitter splitter;
    private final JTextPane information;
    private final StyledDocument styledDocument;

    public AmsToolPanel(Project p, ToolWindow t) {
        super(true, true);
        this.project = p;
        this.toolWindow = t;
        this.splitter = new Splitter(false, 0.75f);
        this.information = new JTextPane();
        this.information.setEditable(false);
        setContent(splitter);
        splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(information));
        information.addMouseListener(new KjjMenu(information));
        this.styledDocument = information.getStyledDocument();
    }

    public AmsToolPanel append(String msg){
        append(msg,Color.BLACK);
        return this;
    }

    public AmsToolPanel append(String msg,Color color){
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr,color);
        try {
            styledDocument.insertString(styledDocument.getLength(),msg,attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return this;
    }

    public AmsToolPanel appendLine(String msg){
        append(msg+"\n");
        return this;
    }

    public AmsToolPanel enterLine(){
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr,Color.BLACK);
        try {
            styledDocument.insertString(styledDocument.getLength(),"\n",attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return this;
    }

    public AmsToolPanel appendLine(String msg , Color color){

        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr,color);
        try {
            styledDocument.insertString(styledDocument.getLength(),msg,attr);
            styledDocument.insertString(styledDocument.getLength(),"\n",attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void appendErrorLine(String msg){
        appendLine(msg,Color.red);
    }

    public void appendWarnLine(String msg){
        appendLine(msg,Color.orange);
    }

    public void appendInfoLine(String msg){
        appendLine(msg,Color.BLUE);
    }

    public AmsToolPanel append(List<ParamBean> apiParamBean) {
        doPrintParamBean(apiParamBean,0);
        return this;
    }

    private void doPrintParamBean(List<ParamBean> apiParamBean,int blankLength) {
        if (CollectionUtils.isEmpty(apiParamBean)){
            return;
        }
        for (ParamBean paramBean : apiParamBean) {
            append(getBlank(blankLength))
            .append(paramBean.getParamKey(),Color.CYAN)
            .append("  :  ")
            .append(paramBean.getParamType().getName(),Color.ORANGE)
            .append("  ;  ");
            if (StringUtils.isBlank(paramBean.getParamName())){
                appendLine("自行填充字段描述",Color.RED);
            }else{
                appendLine(paramBean.getParamName(),Color.YELLOW);
            }
            final List<ParamBean> children = paramBean.getChildren();
            doPrintParamBean(children,blankLength+1);
        }
    }

    public String getBlank(int num){
        if(num==0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i =0 ; i<num*4; i++){
            sb.append(" ");
        }
        return sb.toString();
    }

    public void clear() {
        information.setText("");
    }
}
