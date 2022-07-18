package com.dugq.component.yapi;

import com.dugq.pojo.yapi.YapiConfigBean;
import com.intellij.openapi.ui.DialogWrapper;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author dugq
 * @date 2021/8/11 7:16 下午
 */
public class YapiLoginFormComponent extends DialogWrapper {
    private JTextField email;
    private JTextField password;
    private JTextField host;
    private JRadioButton button1;
    private JRadioButton button2;
    private static final String defaultPassword = "";

    private static final String preloadPassword = "客集集服务不用填写";

    public YapiLoginFormComponent(){
        super(true);
        init();
        setTitle("账号密码");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel parent = new JPanel(new GridLayout(4,2));
        JLabel label = new JLabel("email");
        label.setPreferredSize(new Dimension(50, 40));
        parent.add(label);

        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(100, 30));
        email = textField;
        parent.add(textField);

        JLabel label2 = new JLabel("yapi password");
        label2.setPreferredSize(new Dimension(50, 40));
        JTextField textField2 = new JTextField(20);
        textField2.setText(preloadPassword);
        textField2.setPreferredSize(new Dimension(100, 30));
        parent.add(label2);
        parent.add(textField2);
        password = textField2;

        JLabel label3 = new JLabel("yapi 服务地址");
        label3.setPreferredSize(new Dimension(50, 40));
        JTextField textField3 = new JTextField(20);
        textField3.setText("https://yapi.kjjcrm.com");
        textField3.setPreferredSize(new Dimension(100, 30));
        parent.add(label3);
        parent.add(textField3);
        host = textField3;

        JLabel label4 = new JLabel("登陆方式");
        label4.setPreferredSize(new Dimension(50, 40));
        JPanel jPanel = new JPanel();
        ButtonGroup bg = new ButtonGroup();
        JRadioButton button1 = new JRadioButton("原始登陆",true);
        JRadioButton button2 = new JRadioButton("LDAP");
        bg.add(button1);
        bg.add(button2);
        jPanel.add(button1);
        jPanel.add(button2);
        parent.add(label4);
        parent.add(jPanel);
        this.button1 = button1;
        this.button2 = button2;
        return parent;
    }


    public String getEmail(){
        return email.getText();
    }

    public String getPassword(){
        final String text = password.getText();
        if (StringUtils.isBlank(text) || StringUtils.equals(preloadPassword,text.trim())){
            return defaultPassword;
        }
        return text;
    }

    public String getServer(){
       return host.getText();
    }

    public String getLoginType(){
        if (button1.isSelected()){
            return YapiConfigBean.LOGIN_TYPE_SOURCE;
        }
        if (button2.isSelected()){
            return YapiConfigBean.LOGIN_TYPE_LDAP;
        }
        return null;
    }
}
