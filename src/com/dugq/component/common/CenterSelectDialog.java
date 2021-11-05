package com.dugq.component.common;

import com.dugq.bean.CenterSelectBean;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CollectionComboBoxModel;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/8/12 2:46 下午
 */
public class CenterSelectDialog<T> extends DialogWrapper {
    private List<CenterSelectBean<T>> currentGroupList;
    private List<CenterSelectBean<T>> selectedList = new ArrayList<>();
    private JComboBox box;
    private MyClickButton jButton;

    public CenterSelectDialog( String title,List<CenterSelectBean<T>> currentGroupList){
        super(true);
        this.currentGroupList =currentGroupList;
        setTitle(title);
    }


    public static <K> CenterSelectDialog<K> getInstance(String title, List<K> groupList,Function<K,String> key,Function<K,List<K>> children){
        final List<CenterSelectBean<K>> collect = getCollect(groupList, key, children);
        return new CenterSelectDialog<>(title, collect);
    }

    public static <K> CenterSelectDialog<K> getInstance(String title, List<K> groupList,Function<K,String> key,Function<K,List<K>> children,MyClickButton customButton){
        final List<CenterSelectBean<K>> collect = getCollect(groupList, key, children);
        final CenterSelectDialog<K> kCenterSelectDialog = new CenterSelectDialog<>(title, collect);
        kCenterSelectDialog.jButton = customButton;
        return kCenterSelectDialog;
    }


    @NotNull
    private static <K> List<CenterSelectBean<K>> getCollect(List<K> groupList, Function<K, String> key, Function<K, List<K>> children) {
        return groupList.stream().map(t -> {
            CenterSelectBean<K> bean = new CenterSelectBean<>();
            bean.setName(key.apply(t));
            bean.setId(t);
            if (Objects.nonNull(children)){
                bean.setChildren(getCollect(children.apply(t),key,children));
            }
            return bean;
        }).collect(Collectors.toList());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        if (CollectionUtils.isNotEmpty(currentGroupList)){
            final Map<String, CenterSelectBean<T>> beans = currentGroupList.stream().collect(Collectors.toMap(CenterSelectBean::getName, Function.identity()));
            box = new ComboBox(new MyCollectionComboBoxModel(beans));
            jPanel.add(box);
        }
        if (Objects.nonNull(jButton)){
            jPanel.add(jButton,BorderLayout.SOUTH);
        }
        return jPanel;
    }


    class MyCollectionComboBoxModel extends CollectionComboBoxModel<String>{
        private static final long serialVersionUID = -5303271954336737000L;
        private Map<String,CenterSelectBean<T>> items;

        public MyCollectionComboBoxModel(Map<String,CenterSelectBean<T>> items) {
            super(new ArrayList(items.keySet()));
            this.items = items;
        }

        public CenterSelectBean<T> getMySelected(){
            return items.get(super.getSelected());
        }

        @Nullable
        @Override
        public Object getSelectedItem() {
            return getMySelected();
        }
    }

    public int showAndGetExistCode() {
        init();
        if (CollectionUtils.isEmpty(currentGroupList)){
            super.showAndGet();
            return super.getExitCode();
        }
        while (CollectionUtils.isNotEmpty(currentGroupList)){
            final boolean b = super.showAndGet();
            if (!b){
                return super.getExitCode();
            }
            CenterSelectBean selectedItem = (CenterSelectBean)box.getSelectedItem();
            selectedList.add(selectedItem);
            currentGroupList = selectedItem.getChildren();
        }
        return OK_EXIT_CODE;
    }

    @Override
    public boolean showAndGet() {
        return showAndGetExistCode()==OK_EXIT_CODE;
    }

    @Override
    public void show() {
        init();
        super.show();
    }

    public List<T> getSelectedList() {
        return selectedList.stream().map(CenterSelectBean::getId).collect(Collectors.toList());
    }

    public T getLastSelect(){
        return CollectionUtils.isEmpty(selectedList)?null:selectedList.get(selectedList.size()-1).getId();
    }

    public void setCustomButton(MyClickButton jButton) {
        this.jButton = jButton;
    }
}
