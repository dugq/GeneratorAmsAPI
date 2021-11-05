package com.dugq.component.yapi;

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
    private static final String defaultPassword = "Kjj_123456";

    private static final String preloadPassword = "SSO登陆且未重置过密码的，不要动这里";

    public YapiLoginFormComponent(){
        super(true);
        init();
        setTitle("账号密码");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel parent = new JPanel(new BorderLayout());

        JPanel dialogPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("SSO email");
        label.setPreferredSize(new Dimension(50, 40));
        dialogPanel.add(label,BorderLayout.WEST);
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(100, 30));
        dialogPanel.add(textField,BorderLayout.EAST);
        email = textField;
        parent.add(dialogPanel,BorderLayout.NORTH);

        JPanel dialogPanel2 = new JPanel(new BorderLayout());
        JLabel label2 = new JLabel("yapi password");
        label2.setPreferredSize(new Dimension(50, 40));
        dialogPanel2.add(label2,BorderLayout.WEST);
        JTextField textField2 = new JTextField(20);
        textField2.setText(preloadPassword);
        textField2.setPreferredSize(new Dimension(100, 30));
        dialogPanel2.add(textField2,BorderLayout.EAST);
        password = textField2;
        parent.add(dialogPanel2,BorderLayout.SOUTH);
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
}
