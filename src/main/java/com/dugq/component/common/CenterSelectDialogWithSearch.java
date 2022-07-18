package com.dugq.component.common;

import com.dugq.bean.CenterSelectBean;
import com.intellij.openapi.ui.ComboBox;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 选择弹框
 * @author dugq
 * @date 2021/8/12 2:46 下午
 */
public class CenterSelectDialogWithSearch<T> extends CenterSelectDialog<T> {
    private String defaultSelectedKey;

    public CenterSelectDialogWithSearch(String title, List<CenterSelectBean<T>> currentGroupList){
        super(title,currentGroupList);
    }

    public CenterSelectDialogWithSearch(String title, List<CenterSelectBean<T>> currentGroupList,String defaultSelectedKey){
        super(title,currentGroupList);
        this.defaultSelectedKey = defaultSelectedKey;
    }

    public static <K> CenterSelectDialogWithSearch<K> getSearchInstance(String title, List<K> groupList, Function<K, String> key, Function<K, List<K>> children, String selectedKey){
        final List<CenterSelectBean<K>> collect = getCollect(groupList, key, children);
        return new CenterSelectDialogWithSearch<>(title, collect,selectedKey);
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        final TextField search = new TextField("请搜索");
        search.addTextListener(new TextListener() {
            @Override
            public void textValueChanged(TextEvent e) {
                String value = search.getText();
                if (StringUtils.isBlank(value) || StringUtils.equalsAnyIgnoreCase(value,"请搜索")){
                    final Map<String, CenterSelectBean<T>> beans = currentGroupList.stream().collect(Collectors.toMap(CenterSelectBean::getName, Function.identity()));
                    box.setModel(new MyCollectionComboBoxModel(beans));
                    box.setPopupVisible(true);
                }else{
                    final List<CenterSelectBean<T>> filterList = currentGroupList.stream().filter(group -> group.getName().contains(value)).collect(Collectors.toList());
                    final Map<String, CenterSelectBean<T>> beans = filterList.stream().collect(Collectors.toMap(CenterSelectBean::getName, Function.identity()));
                    box.setModel(new MyCollectionComboBoxModel(beans));
                    box.setPopupVisible(true);
                }
            }
        });
        search.addMouseListener(new MyClickListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton()==1 && StringUtils.equalsAnyIgnoreCase(search.getText(),"请搜索")){
                    search.setText("");
                }
            }
        });
        search.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }
            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtils.isBlank(search.getText())){
                    search.setText("请搜索");
                }
            }
        });
        jPanel.add(search,BorderLayout.NORTH);
        if (CollectionUtils.isNotEmpty(currentGroupList)){
            final Map<String, CenterSelectBean<T>> beans = currentGroupList.stream().collect(Collectors.toMap(CenterSelectBean::getName, Function.identity()));
            box = new ComboBox(new MyCollectionComboBoxModel(beans));
            if (StringUtils.isNotBlank(defaultSelectedKey)){
                box.setSelectedItem(defaultSelectedKey);
            }
            jPanel.add(box,BorderLayout.CENTER);
        }
        if (Objects.nonNull(jButton)){
            jPanel.add(jButton,BorderLayout.SOUTH);
        }
        return jPanel;
    }
}
