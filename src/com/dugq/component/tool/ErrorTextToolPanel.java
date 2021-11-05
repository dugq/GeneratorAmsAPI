package com.dugq.component.tool;

import com.dugq.component.tool.KjjMenu;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ScrollPaneFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Created by dugq on 2021/4/6.
 */
public class ErrorTextToolPanel extends SimpleToolWindowPanel {
    private final Project project;
    private final ToolWindow toolWindow;
    private final Splitter splitter;
    private final JTextPane information;

    public ErrorTextToolPanel(Project p, ToolWindow t) {
        super(true, true);
        this.project = p;
        this.toolWindow = t;
        this.splitter = new Splitter(false, 0.75f);
        this.information = new JTextPane();
        this.information.setEditable(false);
        setContent(splitter);
        splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(information));
        information.addMouseListener(new KjjMenu(information));
    }

    public void append(String msg){
        StyledDocument styledDocument = information.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, Color.red);
        try {
            styledDocument.insertString(styledDocument.getLength(),msg,attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void appendLine(String msg ,Color color){
        StyledDocument styledDocument = information.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr,color);
        try {
            styledDocument.insertString(styledDocument.getLength(),msg,attr);
            styledDocument.insertString(styledDocument.getLength(),"\n",attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
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

    public void enterLine(){
        StyledDocument styledDocument = information.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, Color.red);
        try {
            styledDocument.insertString(styledDocument.getLength(),"\n",attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        information.setText("");
    }
}
