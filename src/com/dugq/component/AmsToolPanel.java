package com.dugq.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ScrollPaneFactory;

import javax.swing.*;

/**
 * Created by dugq on 2021/4/6.
 */
public class AmsToolPanel extends SimpleToolWindowPanel {
    public static final String PanelId = "API Print";

    private final Project project;
    private final ToolWindow toolWindow;
    private final Splitter splitter;
    private final JTextArea information;

    public AmsToolPanel(Project p, ToolWindow t) {
        super(true, true);
        this.project = p;
        this.toolWindow = t;
        this.splitter = new Splitter(false, 0.75f);
        this.information = new JTextArea();
        this.information.setEditable(false);
        setContent(splitter);
        splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(information));
        information.addMouseListener(new KjjMenu(information));
    }

    public void append(String msg){
        information.append(msg);
    }

    public void appendLine(String msg){
        information.append(msg);
        information.append("\n");
    }

    public void enterLine(){
        information.append("\n");
    }

}
