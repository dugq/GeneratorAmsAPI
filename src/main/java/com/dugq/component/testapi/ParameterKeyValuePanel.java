package com.dugq.component.testapi;

import com.dugq.component.common.MyClickButton;
import com.dugq.pojo.KeyValueBean;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/7/8 5:36 下午
 * {@link ParameterKeyValuePanel} 用于保存API的paramBeanList、展示所有的实际参数、操作实际参数 所有的参数列表。
 * {@link ParameterKeyValueLinePanel} 用于展示单个有效参数，非Object类型paramBean
 * {@link #keyValueBeanList} 用于保存所有展示有效的参数的panel。每个panel中会显示的展示它的所有父key链表，用.隔开
 *
 */
public class ParameterKeyValuePanel extends JPanel {

    private final Project project;

    private final List<ParameterKeyValueLinePanel> keyValueBeanList = new ArrayList<>();


    public ParameterKeyValuePanel(Project project) {
        this.project = project;
        //init key value content
        this.setLayout(new VerticalFlowLayout(0,0,0,true,false));

        this.add(titlePanel());
    }

    private JPanel titlePanel(){
        JPanel titlePanel = new JPanel();
        int width = this.getWidth();
        if (width<1150){
            width = 1150;
        }
        JTextField myName = new JTextField("Filed Name",width*20/1150);
        myName.setBackground(Color.CYAN);
        myName.setForeground(Color.BLACK);
        myName.setHorizontalAlignment(SwingConstants.CENTER);
        myName.setEditable(false);
        titlePanel.add(myName);

        JTextField myValue = new JTextField("Filed Value",width*15/1150);
        myValue.setBackground(Color.CYAN);
        myValue.setForeground(Color.BLACK);
        myValue.setHorizontalAlignment(SwingConstants.CENTER);
        myValue.setEditable(false);
        titlePanel.add(myValue);

        JTextField myDesc = new JTextField("Filed Desc",width*12/1150);
        myDesc.setBackground(Color.CYAN);
        myDesc.setForeground(Color.BLACK);
        myDesc.setHorizontalAlignment(SwingConstants.CENTER);
        myDesc.setEditable(false);
        titlePanel.add(myDesc);

        titlePanel.add(new MyClickButton("+", (e)-> addPanel(new ParameterKeyValueLinePanel(this,null))));

        return titlePanel;
    }

    void removeKeyValuePanel(ParameterKeyValueLinePanel panel) {
        this.keyValueBeanList.remove(panel);
        super.remove(panel);
        super.updateUI();
    }

    void addPanel(ParameterKeyValueLinePanel child){
        super.add(child);
        keyValueBeanList.add(child);
        super.updateUI();
    }

    public void init(List<KeyValueBean> keyValueBeans){
        clear();
        if (CollectionUtils.isEmpty(keyValueBeans)){
            return;
        }
        for (KeyValueBean keyValueBean : keyValueBeans) {
            addPanel(new ParameterKeyValueLinePanel(this,keyValueBean));
        }
        super.updateUI();
    }

    public List<KeyValueBean> getContent(){
        return getKeyValueBeanList().stream().map(ParameterKeyValueLinePanel::getContent).collect(Collectors.toList());
    }

    public List<ParameterKeyValueLinePanel> getKeyValueBeanList() {
        return new ArrayList<>(keyValueBeanList);
    }

    public void clear() {
        for (ParameterKeyValueLinePanel panel : getKeyValueBeanList()) {
            panel.removeSelf();
        }
    }

}
