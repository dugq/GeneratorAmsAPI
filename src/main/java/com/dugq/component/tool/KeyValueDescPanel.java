package com.dugq.component.tool;

import com.dugq.component.common.MyClickButton;
import com.dugq.pojo.KeyValueBean;
import com.dugq.service.config.impl.KeyValueConfigService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/7/8 5:36 下午
 */
public class KeyValueDescPanel extends JScrollPane {

    private final Project project;

    private List<KeyValueDescLinePanel> keyValueBeanList = new ArrayList<>();

    private final JPanel content = new JPanel();

    private final KeyValueConfigService service;

    private int minLength = 1150;

    public <T extends KeyValueConfigService> KeyValueDescPanel(Project project, Class<T> serviceClass) {
        service = project.getService(serviceClass);
        this.project = project;
        this.content.setLayout(new VerticalFlowLayout(0,0,0,true,false));

        super.setViewportView(content);
        JPanel headerPanel = buildHeaderPanel();
        content.add(headerPanel);
        init(service.getList());

    }

    @NotNull
    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel();
        int width = this.getWidth();
        if (width<minLength){
            width = 1150;
        }
        final JTextField key = new JTextField("KEY", width * 15 / width);
        key.setEditable(false);
        headerPanel.add(key);
        final JTextField value = new JTextField("value", width * 30 / width);
        value.setEditable(false);
        headerPanel.add(value);
        final JTextField desc = new JTextField("DESC", width * 30 / width);
        desc.setEditable(false);
        headerPanel.add(desc);
        KeyValueDescPanel that = this;
        MyClickButton addButton = new MyClickButton("+",(e)-> that.addPanel(new KeyValueDescLinePanel(that,null)));
        headerPanel.add(addButton);
        return headerPanel;
    }

    void removeKeyValuePanel(KeyValueDescLinePanel panel) {
        if (keyValueBeanList.size()<1){
            panel.clear();
            return;
        }
        this.keyValueBeanList.remove(panel);
        content.remove(panel);
        service.delete(panel.getKeyValue());
    }

    void addPanel(KeyValueDescLinePanel child){
        content.add(child);
        keyValueBeanList.add(child);
        service.save(child.getKeyValue());
    }

    public List<KeyValueBean> getAllKeyValueBeans(){
        return keyValueBeanList.stream().map(KeyValueDescLinePanel::getKeyValue).collect(Collectors.toList());
    }

    public Map<String,String> getAllKeyValue(){
        return keyValueBeanList.stream().collect(Collectors.toMap(c->c.getKeyValue().getKey(),c->c.getKeyValue().getValue(),(left,right)->right));
    }

    public void init(List<KeyValueBean> keyValueBeans){
        if (CollectionUtils.isEmpty(keyValueBeans)){
            return;
        }
        keyValueBeans.forEach(keyValueBean -> {
            final KeyValueDescLinePanel childPanel = new KeyValueDescLinePanel(this,keyValueBean);
            content.add(childPanel);
            keyValueBeanList.add(childPanel);
        });
    }

}
