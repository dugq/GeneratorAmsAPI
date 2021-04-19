package com.dugq.bean;

import com.dugq.component.MyClickListener;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2021/4/7.
 */
public class MyJPanelLine {

    private JTextField name;
    private JTextField value;
    private JPanel jPanel;

    public MyJPanelLine(JPanel parentPanel, Map<JTextField,JTextField> container) {
        this.jPanel = new JPanel();
        this.name = new JTextField(12);
        jPanel.add(new JLabel("key:"));
        jPanel.add(name);
        this.value = new JTextField(12);
        jPanel.add(new JLabel("value:"));
        jPanel.add(value);
        JButton deleteButton = new JButton("-");
        deleteButton.setPreferredSize(new Dimension(30,30));
        deleteButton.addMouseListener(new MyClickListener(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    parentPanel.remove(jPanel);
                    container.remove(name);
                    parentPanel.updateUI();
                }
            }
        });
        jPanel.add(deleteButton);
        container.put(name,value);
        parentPanel.add(jPanel);
        parentPanel.updateUI();
    }

    public JTextField getName() {
        return name;
    }

    public JTextField getValue() {
        return value;
    }

    public JPanel getjPanel() {
        return jPanel;
    }


    public static void addKeyValue(String key,String value,JPanel parentPanel,Map<JTextField,JTextField> container){
        MyJPanelLine myJPanelLine = new MyJPanelLine(parentPanel, container);
        myJPanelLine.getName().setText(key);
        myJPanelLine.getValue().setText(value);
        myJPanelLine.getjPanel().updateUI();
    }

    public static Map<String,String> getAllKeyValueMap(Map<JTextField,JTextField> container){
        Map<String,String> map = new HashMap<>();
        for (Map.Entry<JTextField, JTextField> entry : container.entrySet()) {
            String key = entry.getKey().getText();
            String value = entry.getValue().getText();
            if (StringUtils.isBlank(key) || StringUtils.isBlank(value)){
                break;
            }
            map.put(key,value);
        }
        return map;
    }

}
