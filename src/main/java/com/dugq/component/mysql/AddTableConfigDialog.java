package com.dugq.component.mysql;

import com.dugq.pojo.mybatis.ConfigBean;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
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
public class InitConfigDialog extends DialogWrapper {
    private final JBTextField xmlPath = new JBTextField(30);
    private final JBTextField entityPath = new JBTextField(30);
    private final JBTextField daoPath = new JBTextField(30);
    private final JBTextField dbUrl = new JBTextField(30);
    private final JBTextField dbUserName = new JBTextField(30);
    private final JBTextField dbPwd =new JBTextField(30) ;
    private final JBTextField subModule = new JBTextField(30);


    public InitConfigDialog(@Nullable Project project) {
        super(project,true);
        setTitle("初始化配置");
    }
    private final String label1 = "XML的包路径(必填)";
    private final String label2 = "Entity包路径(必填)";
    private final String label3 = "Dao包路径(必填)";
    private final String label4 = "MysqlDbUrl(必填)";
    private final String label5 = "MysqlUserName(必填)";
    private final String label6 = "MysqlPassword(必填)";
    private final String label7 = "子模块名称";

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final CellConstraints cc = new CellConstraints();
        JPanel dialogPanel = new JPanel(new FormLayout("150px,400px","30px,30px,30px,30px,30px,30px,30px,30px"));
        dialogPanel.add(new JBLabel(label1),cc.xy(1,1));
        dialogPanel.add(xmlPath,cc.xy(2,1));

        dialogPanel.add(new JBLabel(label2),cc.xy(1,2));
        dialogPanel.add(entityPath,cc.xy(2,2));

        dialogPanel.add(new JBLabel(label3),cc.xy(1,3));
        dialogPanel.add(daoPath,cc.xy(2,3));

        dialogPanel.add(new JBLabel(label4),cc.xy(1,4));
        dialogPanel.add(dbUrl,cc.xy(2,4));

        dialogPanel.add(new JBLabel(label5),cc.xy(1,5));
        dialogPanel.add(dbUserName,cc.xy(2,5));

        dialogPanel.add(new JBLabel(label6),cc.xy(1,6));
        dialogPanel.add(dbPwd,cc.xy(2,6));

        dialogPanel.add(new JBLabel(label7),cc.xy(1,7));
        dialogPanel.add(subModule,cc.xy(2,7));

        dialogPanel.add(new JBLabel("文件路径:"),cc.xy(1,8));
        dialogPanel.add(new JBLabel("项目路径/子模块路径/src/main/java(resource)/包路径/子包名/文件"),cc.xy(2,8));
        return dialogPanel;
    }

    public ConfigBean getConfig(){
        final ConfigBean configBean = new ConfigBean();
        configBean.setDaoPath(daoPath.getText());
        configBean.setDbPwd(dbPwd.getText());
        configBean.setDbUrl(dbUrl.getText());
        configBean.setDbUserName(dbUserName.getText());
        configBean.setSubModule(subModule.getText());
        configBean.setMapperPath(xmlPath.getText());
        configBean.setEntityPath(entityPath.getText());
        return configBean;
    }

    public void initDefaultValue(ConfigBean configBean) {
        if (Objects.nonNull(configBean)){
            xmlPath.setText(configBean.getMapperPath());
            entityPath.setText(configBean.getEntityPath());
            daoPath.setText(configBean.getDaoPath());
            dbUrl.setText(configBean.getDbUrl());
            dbUserName.setText(configBean.getDbUserName());
            dbPwd.setText(configBean.getDbPwd());
            subModule.setText(configBean.getSubModule());
        }
        init();
    }
}
