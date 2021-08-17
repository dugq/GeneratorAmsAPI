package com.dugq.component.ams;

import com.dugq.pojo.ams.GroupVo;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Created by dugq on 2019/12/27.
 */
public class SelectInputComponent extends DialogWrapper {
    private List<GroupVo> groupList;
    private JComboBox box;

    public SelectInputComponent(List<GroupVo> groupList,String uri){
        super(true);
        this.groupList = groupList;
        init();
        setTitle("请选择接口"+uri+"的分组");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel parent = new JPanel(new BorderLayout());
        parent.setBorder(new EmptyBorder(15,15,15,15));
        JComboBox box = new JComboBox();
        this.box = box;
        for (GroupVo  group: groupList) {
            box.addItem(group.getGroupName());
        }
        parent.add(box,BorderLayout.CENTER);
        return parent;
    }

    public JComboBox getBox() {
        return box;
    }
}
