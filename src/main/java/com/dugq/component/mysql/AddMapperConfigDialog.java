package com.dugq.component.mysql;

import com.dugq.pojo.enums.MapperOpEnums;
import com.dugq.pojo.mybatis.AppendMapperConfigBean;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2022/6/29 10:51 上午
 */
public class AddMapperConfigDialog extends DialogWrapper {
    private List<String> allColumns;
    private List<String> allIndexColumns;
    private ComboBox<MapperOpEnums> comboBoxWithHistory;
    private JTextField methodName = new JBTextField();
    private JTextField desc = new JBTextField();
    private JTextArea insertColumns = new JBTextArea(5,20);
    private JTextArea updateColumns = new JBTextArea(5,20);
    private JTextArea selectColumns = new JBTextArea(5,20);
    private JTextArea whereColumns = new JBTextArea(3,20);
    private JTextField genParamName = new JTextField();
    private JTextField genDtoName = new JTextField();
    private JTextField genEntityName = new JTextField();
    private JPanel sonPanel;
    private Project project;

    public AddMapperConfigDialog(@Nullable Project project, List<String> allColumns, List<String> allIndexColumns) {
        super(project,true);
        this.project = project;
        setTitle("追加mapper");
        insertColumns.addMouseListener(new MyMouseListener(insertColumns));
        updateColumns.addMouseListener(new MyMouseListener(updateColumns));
        selectColumns.addMouseListener(new MyMouseListener(selectColumns));
        whereColumns.addMouseListener(new MyMouseListener(whereColumns));
        sonPanel = new JPanel(new FormLayout("150px,200px","100px,50px,30px,30px,30px"));
        comboBoxWithHistory = new ComboBox<>(MapperOpEnums.values());
        comboBoxWithHistory.addItemListener(item-> fillSonPanel((MapperOpEnums)item.getItem()));
        this.allColumns = allColumns;
        this.allIndexColumns = allIndexColumns;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new VerticalFlowLayout());
        final CellConstraints cc = new CellConstraints();
        JPanel dialogPanel = new JPanel(new FormLayout("150px,200px","30px,30px,30px"));
        dialogPanel.add(new JBLabel("操作类型(必填)"),cc.xy(1,1));
        dialogPanel.add(comboBoxWithHistory,cc.xy(2,1));
        dialogPanel.add(new JBLabel("方法名(必填)"),cc.xy(1,2));
        dialogPanel.add(methodName,cc.xy(2,2));
        dialogPanel.add(new JBLabel("方法描述(必填)"),cc.xy(1,3));
        dialogPanel.add(desc,cc.xy(2,3));
        mainPanel.add(dialogPanel);
        fillSonPanel(((MapperOpEnums)comboBoxWithHistory.getSelectedItem()));
        mainPanel.add(sonPanel);
        return mainPanel;
    }

    private void fillSonPanel(MapperOpEnums item) {
        final CellConstraints cc = new CellConstraints();
        sonPanel.removeAll();
        if (item==MapperOpEnums.INSERT){
            sonPanel.add(wrapperTextArea(new JBLabel("插入字段(必填)")),cc.xy(1,1));
            sonPanel.add(ScrollPaneFactory.createScrollPane(insertColumns),cc.xy(2,1));
            sonPanel.add(wrapperTextArea(new JBLabel("入参名称(非必填)"),"自动在前置param路径处生成新的param，为空时不生成param，所有字段将会一字排开"),cc.xy(1,2));
            sonPanel.add(genParamName,cc.xy(2,2));
        }else if (item==MapperOpEnums.UPDATE){
            sonPanel.add(wrapperTextArea(new JBLabel("更新字段(必填)")),cc.xy(1,1));
            sonPanel.add(ScrollPaneFactory.createScrollPane(updateColumns),cc.xy(2,1));
            sonPanel.add(wrapperTextArea(new JBLabel("更新条件(必填)")),cc.xy(1,2));
            sonPanel.add(ScrollPaneFactory.createScrollPane(whereColumns),cc.xy(2,2));
            sonPanel.add(wrapperTextArea(new JBLabel("入参名称(非必填)"),"自动在前置param路径处生成新的param，为空时不生成param，所有字段将会一字排开"),cc.xy(1,3));
            sonPanel.add(genParamName,cc.xy(2,3));
        }else if (item==MapperOpEnums.SELECT){
            sonPanel.add(wrapperTextArea(new JBLabel("查询字段(必填)")),cc.xy(1,1));
            sonPanel.add(ScrollPaneFactory.createScrollPane(selectColumns),cc.xy(2,1));
            sonPanel.add(wrapperTextArea(new JBLabel("查询条件(必填)")),cc.xy(1,2));
            sonPanel.add(ScrollPaneFactory.createScrollPane(whereColumns),cc.xy(2,2));
            sonPanel.add(wrapperTextArea(new JBLabel("入参名称(非必填)"),"自动在前置param路径处生成新的param，为空时不生成param，所有字段将会一字排开"),cc.xy(1,3));
            sonPanel.add(genParamName,cc.xy(2,3));
            sonPanel.add(wrapperTextArea(new JBLabel("查询结果entity(非必填)"),"自动在前置entity路径处生成新的Entity，为空时不生成新的Entity，自动引用domainEntity，无domainEntity将报错"),cc.xy(1,4));
            sonPanel.add(genEntityName,cc.xy(2,4));
            sonPanel.add(wrapperTextArea(new JBLabel("Dto名称(非必填)"),"自动在前置Dto路径处生成新的Dto，为空时不生成Dto，自动引用domainDto.无domainDto将报错"),cc.xy(1,5));
            sonPanel.add(genDtoName,cc.xy(2,5));
        }else if (item==MapperOpEnums.DELETE){
            sonPanel.add(wrapperTextArea(new JBLabel("删除条件(必填)")),cc.xy(1,1));
            sonPanel.add(ScrollPaneFactory.createScrollPane(whereColumns),cc.xy(2,1));
            sonPanel.add(wrapperTextArea(new JBLabel("入参名称(非必填)"),"自动在前置param路径处生成新的param，为空时不生成param，所有字段将会一字排开"),cc.xy(1,2));
            sonPanel.add(genParamName,cc.xy(2,2));
        }else if (item==MapperOpEnums.BATCH_INSERT){
            sonPanel.add(wrapperTextArea(new JBLabel("插入字段(必填)")),cc.xy(1,1));
            sonPanel.add(ScrollPaneFactory.createScrollPane(insertColumns),cc.xy(2,1));
            sonPanel.add(wrapperTextArea(new JBLabel("入参名称(非必填)"),"自动在前置param路径处生成新的param，为空时不生成param，自动引用domainEntity"),cc.xy(1,2));
            sonPanel.add(genParamName,cc.xy(2,2));
        }else if (item==MapperOpEnums.BATCH_SELECT){
            sonPanel.add(wrapperTextArea(new JBLabel("查询字段(必填)")),cc.xy(1,1));
            sonPanel.add(ScrollPaneFactory.createScrollPane(selectColumns),cc.xy(2,1));
            sonPanel.add(wrapperTextArea(new JBLabel("查询条件(必填)")),cc.xy(1,2));
            sonPanel.add(ScrollPaneFactory.createScrollPane(whereColumns),cc.xy(2,2));
            sonPanel.add(wrapperTextArea(new JBLabel("入参名称(非必填)"),"自动在前置param路径处生成新的param，为空时不生成param，所有字段将会一字排开"),cc.xy(1,3));
            sonPanel.add(genParamName,cc.xy(2,3));
            sonPanel.add(wrapperTextArea(new JBLabel("查询结果entity(非必填)"),"自动在前置entity路径处生成新的Entity，为空时不生成新的Entity，自动引用domainEntity，无domainEntity将报错"),cc.xy(1,4));
            sonPanel.add(genEntityName,cc.xy(2,4));
            sonPanel.add(wrapperTextArea(new JBLabel("Dto名称(非必填)"),"自动在前置Dto路径处生成新的Dto，为空时不生成Dto，自动引用domainDto.无domainDto将报错"),cc.xy(1,5));
            sonPanel.add(genDtoName,cc.xy(2,5));
        }
        sonPanel.updateUI();
    }

    private JBLabel wrapperTextArea(JBLabel label){
        return wrapperTextArea(label,"双击输入框可弹出表的所有列");
    }

    private JBLabel wrapperTextArea(JBLabel label,String tips){
        label.setToolTipText(tips);
        return label;
    }


    public AppendMapperConfigBean getConfig(){
        final AppendMapperConfigBean tableConfigBean = new AppendMapperConfigBean();
        final Integer type = ((MapperOpEnums) comboBoxWithHistory.getSelectedItem()).getType();
        tableConfigBean.setOpEnums(type);
        tableConfigBean.setMethodName(methodName.getName());
        tableConfigBean.setGenerateDtoName(genDtoName.getText());
        if (type == MapperOpEnums.SELECT.getType() || type == MapperOpEnums.BATCH_SELECT.getType()){
            tableConfigBean.setGenerateEntityName(genEntityName.getText());
        }
        tableConfigBean.setGenerateParamName(genParamName.getText());
        if (type == MapperOpEnums.INSERT.getType() || type == MapperOpEnums.BATCH_INSERT.getType()){
            tableConfigBean.setInsertColumns(getTextArea(insertColumns));
        }
        if (type == MapperOpEnums.SELECT.getType() || type == MapperOpEnums.BATCH_SELECT.getType()){
            tableConfigBean.setSelectColumns(getTextArea(selectColumns));
        }
        if (type == MapperOpEnums.UPDATE.getType()){
            tableConfigBean.setUpdateColumns(getTextArea(updateColumns));
        }
        if (type != MapperOpEnums.INSERT.getType() && type != MapperOpEnums.BATCH_INSERT.getType()){
            tableConfigBean.setWhereColumns(getTextArea(whereColumns));
        }
        tableConfigBean.setMethodName(methodName.getText());
        tableConfigBean.setDesc(desc.getText());
        return tableConfigBean;
    }

    public void initDefaultValue(AppendMapperConfigBean tableConfigBean) {
        if (Objects.nonNull(tableConfigBean)){
            final MapperOpEnums byType = MapperOpEnums.getByType(tableConfigBean.getOpEnums());
            if (Objects.nonNull(byType)){
                comboBoxWithHistory.setSelectedItem(byType);
            }
            clearAndSet2TextArea(tableConfigBean.getUpdateColumns(),updateColumns);
            clearAndSet2TextArea(tableConfigBean.getSelectColumns(),selectColumns);
            clearAndSet2TextArea(tableConfigBean.getInsertColumns(),insertColumns);
            clearAndSet2TextArea(tableConfigBean.getWhereColumns(),whereColumns);
            genDtoName.setText(tableConfigBean.getGenerateDtoName());
            genParamName.setText(tableConfigBean.getGenerateParamName());
            genEntityName.setText(tableConfigBean.getGenerateEntityName());
            methodName.setText(tableConfigBean.getMethodName());
            desc.setText(tableConfigBean.getDesc());
        }
        init();
    }

    private List<String> getTextArea(JTextArea textArea){
        final String text = textArea.getText();
        if (StringUtils.isBlank(text)){
            return Collections.emptyList();
        }
        final String[] columns = text.split("\n");
        return Arrays.asList(columns);
    }

    private void clearAndSet2TextArea(Collection<String> columns, JTextArea textArea){
        if (CollectionUtils.isEmpty(columns)){
            return;
        }
        final String text = columns.stream().filter(column->allColumns.contains(column)).collect(Collectors.joining("\n"));
        textArea.setText(text);
    }

    private void append2TextArea(Collection<String> columns, JTextArea textArea){
        if (CollectionUtils.isEmpty(columns)){
            return;
        }
        final List<String> sourceColumns = getTextArea(textArea);
        if (CollectionUtils.isEmpty(sourceColumns)){
            clearAndSet2TextArea(columns, textArea);
        }
        columns.forEach(column->{
            if (!sourceColumns.contains(column)){
                textArea.append("\n"+column);
            }
        });
    }


    class MyMouseListener implements MouseListener {
        private JTextArea myTextArea;

        public MyMouseListener(JTextArea textArea) {
            this.myTextArea = textArea;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) return;
            StringMultiChooserDialogImpl chooser = new StringMultiChooserDialogImpl(project,allColumns);
            final boolean result = chooser.showAndGet();
            if (result){
                final List<String> selectedList = chooser.getSelectedList();
                clearAndSet2TextArea(selectedList,myTextArea);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }


}
