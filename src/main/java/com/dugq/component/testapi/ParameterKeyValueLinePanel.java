package com.dugq.component.testapi;

import com.dugq.component.common.MyClickButton;
import com.dugq.pojo.KeyValueBean;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Created by dugq on 2021/4/7.
 */
public class ParameterKeyValueLinePanel extends JPanel{

    private final JTextField myName;
    private final JTextField myValue;
    private final JTextField myDesc;
    private final JButton deleteButton;
    private final ParameterKeyValuePanel parentPanel;

    //写代码是适配最小屏幕
    private static final int minLength = 1150;
    private final KeyValueBean content;

    public ParameterKeyValueLinePanel(ParameterKeyValuePanel parentPanel, KeyValueBean paramBean) {
        this.parentPanel = parentPanel;
        int width = parentPanel.getWidth();
        if (width<minLength){
            width = minLength;
        }
        this.myName = new JTextField(width*20/1150);
        myName.setBackground(Color.white);
        myName.setForeground(Color.black);
        add(myName);

        this.myValue = new JTextField(width*15/1150);
        myValue.setBackground(Color.white);
        myValue.setForeground(Color.PINK);
        add(myValue);

        this.myDesc = new JTextField(width*12/1150);
        myDesc.setBackground(Color.white);
        add(myDesc);

        deleteButton = new MyClickButton("-",(e)->removeSelf());
        add(deleteButton);

        if (Objects.nonNull(paramBean)){
            myName.setEditable(false);
            myName.setText(paramBean.getKey());
            myValue.setText(paramBean.getValue());
            myDesc.setText(paramBean.getDesc());
        }
        this.content = new KeyValueBean();
    }

    public KeyValueBean getContent(){
        content.setKey(this.myName.getText());
        content.setValue(this.myValue.getText());
        content.setDesc(this.myDesc.getText());
       return content;
    }

    public void removeSelf() {
        this.parentPanel.removeKeyValuePanel(this);
    }

}
