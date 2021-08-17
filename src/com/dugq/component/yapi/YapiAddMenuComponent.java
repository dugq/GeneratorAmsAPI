package com.dugq.component.yapi;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author dugq
 * @date 2021/8/11 7:16 下午
 */
public class YapiAddMenuComponent extends DialogWrapper {
    private JTextField name;
    private JTextField desc;

    public YapiAddMenuComponent(){
        super(true);
        init();
        setTitle("新增接口分类");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel parent = new JPanel(new BorderLayout());
        JPanel dialogPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("name");
        label.setPreferredSize(new Dimension(50, 20));
        dialogPanel.add(label,BorderLayout.WEST);
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(100, 30));
        dialogPanel.add(textField,BorderLayout.EAST);
        name = textField;
        parent.add(dialogPanel,BorderLayout.NORTH);

        JPanel dialogPanel2 = new JPanel(new BorderLayout());
        JLabel label2 = new JLabel("desc");
        label2.setPreferredSize(new Dimension(50, 20));
        dialogPanel2.add(label2,BorderLayout.WEST);
        JTextField textField2 = new JTextField(20);
        textField2.setPreferredSize(new Dimension(100, 30));
        dialogPanel2.add(textField2,BorderLayout.EAST);
        desc = textField2;
        parent.add(dialogPanel2,BorderLayout.SOUTH);
        return parent;
    }

    public String getName(){
        return name.getText();
    }

    public String getDesc(){
        return desc.getText();
    }
}
