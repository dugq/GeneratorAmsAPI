package com.dugq.component.ams;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dugq on 2019/12/27.
 */
public class AmsLoginFormComponent extends DialogWrapper {
    private JTextField account;
    private JTextField password;

    public AmsLoginFormComponent(){
        super(true);
        init();
        setTitle("账号密码");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel parent = new JPanel(new BorderLayout());

        JPanel dialogPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("账号");
        label.setPreferredSize(new Dimension(50, 10));
        dialogPanel.add(label,BorderLayout.WEST);
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(100, 30));
        dialogPanel.add(textField,BorderLayout.EAST);
        account = textField;
        parent.add(dialogPanel,BorderLayout.NORTH);

        JPanel dialogPanel2 = new JPanel(new BorderLayout());
        JLabel label2 = new JLabel("密码");
        label2.setPreferredSize(new Dimension(50, 10));
        dialogPanel2.add(label2,BorderLayout.WEST);
        JTextField textField2 = new JTextField(20);
        textField2.setPreferredSize(new Dimension(100, 30));
        dialogPanel2.add(textField2,BorderLayout.EAST);
        password = textField2;


        parent.add(dialogPanel2,BorderLayout.SOUTH);
        return parent;
    }

    public String getAccount(){
        return account.getText();
    }
    public String getPassword(){
        return password.getText();
    }
}
