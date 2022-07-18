package com.dugq.component.mysql;

import com.dugq.pojo.mybatis.TableConfigBean;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * @author dugq
 * @date 2022/6/29 10:51 上午
 */
public class AddTableConfigDialog extends DialogWrapper {
    private final JBCheckBox genDto = new JBCheckBox();
    private final JBCheckBox genParam = new JBCheckBox();
    private final JBTextField subPackage = new JBTextField();
    private final JBTextField domain = new JBTextField();
    private final JBTextField tableName = new JBTextField();

    public AddTableConfigDialog(@Nullable Project project) {
        super(project,true);
        setTitle("表配置");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final CellConstraints cc = new CellConstraints();
        JPanel dialogPanel = new JPanel(new FormLayout("150px,200px","30px,30px,30px,30px,30px"));
        dialogPanel.add(new JBLabel("表名(必填)"),cc.xy(1,1));
        dialogPanel.add(tableName,cc.xy(2,1));

        dialogPanel.add(new JBLabel("实体名称(必填)"),cc.xy(1,2));
        dialogPanel.add(domain,cc.xy(2,2));

        dialogPanel.add(new JBLabel("子包名称"),cc.xy(1,3));
        dialogPanel.add(subPackage,cc.xy(2,3));

        dialogPanel.add(new JBLabel("是否生成dto"),cc.xy(1,4));
        dialogPanel.add(genDto,cc.xy(2,4));

        dialogPanel.add(new JBLabel("是否生成param"),cc.xy(1,5));
        dialogPanel.add(genParam,cc.xy(2,5));

        return dialogPanel;
    }

    public TableConfigBean getConfig(){
        final TableConfigBean tableConfigBean = new TableConfigBean();
        tableConfigBean.setDomain(domain.getText());
        tableConfigBean.setGenerateDto(genDto.isSelected());
        tableConfigBean.setSubPackage(subPackage.getText());
        tableConfigBean.setTableName(tableName.getText());
        tableConfigBean.setGenerateParam(genParam.isSelected());
        return tableConfigBean;
    }

    public void initDefaultValue(TableConfigBean tableConfigBean) {
        if (Objects.nonNull(tableConfigBean)){
            genDto.setSelected(tableConfigBean.isGenerateDto());
            subPackage.setText(tableConfigBean.getSubPackage());
            domain.setText(tableConfigBean.getDomain());
            tableName.setText(tableConfigBean.getTableName());
            genParam.setSelected(tableConfigBean.isGenerateParam());
        }
        init();
    }
}
