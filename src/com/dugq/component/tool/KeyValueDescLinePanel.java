package com.dugq.component.tool;

import com.dugq.component.common.MyClickButton;
import com.dugq.pojo.KeyValueBean;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Objects;

/**
 * Created by dugq on 2021/4/7.
 */
public class KeyValueDescLinePanel extends JPanel{

    private JTextField myName;
    private JTextField myValue;
    private JTextField myDesc;
    private JButton deleteButton;
    private JButton addButton;

    private KeyValueBean keyValueBean;
    //写代码是适配最小屏幕
    private int minLength = 1150;

    public KeyValueDescLinePanel(KeyValueDescPanel parentPanel,KeyValueBean keyValueBean) {
        KeyValueDescLinePanel that = this;
        if (Objects.isNull(keyValueBean)){
            keyValueBean = new KeyValueBean();
        }
        this.keyValueBean = keyValueBean;

        int width = parentPanel.getWidth();
        if (width<minLength){
            width = 1150;
        }
        this.myName = new JTextField(keyValueBean.getKey(),width*15/1150);
        add(myName);
        myName.addFocusListener(getListener(this));

        this.myValue = new JTextField(keyValueBean.getValue(),width*30/1150);
        add(myValue);
        myValue.addFocusListener(getListener(this));

        this.myDesc = new JTextField(keyValueBean.getDesc(),width*30/1150);
        add(myDesc);

        myDesc.addFocusListener(getListener(this));

        deleteButton = new MyClickButton("-",(e)->{
            parentPanel.removeKeyValuePanel(that);
            parentPanel.updateUI();
        });
        add(deleteButton);
        parentPanel.updateUI();
    }

    @NotNull
    private FocusListener getListener( KeyValueDescLinePanel sonPanel) {
        return new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                sonPanel.getKeyValue();
            }
        };
    }

    public KeyValueBean getKeyValue(){
        keyValueBean.setKey(myName.getText());
        keyValueBean.setValue(myValue.getText());
        keyValueBean.setDesc(myDesc.getText());
       return keyValueBean;
    }

    public KeyValueBean get(){
        return new KeyValueBean(myName.getText(),myValue.getText(),myDesc.getText());
    }


    public void clear() {
        myName.setText("");
        myDesc.setText("");
        myValue.setText("");
    }
}
