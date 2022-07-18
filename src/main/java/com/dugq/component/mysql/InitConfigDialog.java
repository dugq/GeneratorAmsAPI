package com.dugq.component.mysql;

import com.dugq.pojo.mybatis.MySqlConfigBean;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogPanel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
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
    private final JBTextField xmlPackagePath = new JBTextField(50);
    private final JBTextField xmlRootPath = new JBTextField(50);
    private final JBTextField entityPackagePath = new JBTextField(50);
    private final JBTextField entityRootPath = new JBTextField(50);
    private final JBTextField daoPackagePath = new JBTextField(50);
    private final JBTextField daoRootPath = new JBTextField(50);
    private final JBTextField dbUrl = new JBTextField(50);
    private final JBTextField dbUserName = new JBTextField(50);
    private final JBTextField dbPwd =new JBTextField(50) ;
    private final JBTextField dtoPackagePath =new JBTextField(50) ;
    private final JBTextField dtoRootPath =new JBTextField(50) ;
    private final JBTextField paramPackagePath =new JBTextField(50) ;
    private final JBTextField paramRootPath =new JBTextField(50) ;


    public InitConfigDialog(@Nullable Project project) {
        super(project,true);
        setTitle("初始化配置");
    }
    private final String label1 = "XML的源码路径和包路径(必填)";
    private final String label2 = "Entity源码路径和包路径(必填)";
    private final String label3 = "Dao源码路径和包路径(必填)";
    private final String label4 = "MysqlDbUrl(必填)";
    private final String label5 = "MysqlUserName(必填)";
    private final String label6 = "MysqlPassword(必填)";
    private final String label7 = "dto的源码路径和包路径(必填)";
    private final String label8 = "param的源码路径和包路径(必填)";

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final CellConstraints cc = new CellConstraints();

        JPanel parent = new DialogPanel(new VerticalFlowLayout(0));


        JPanel firstDialogPanel = new DialogPanel(new FormLayout("150px,600px","30px,30px,30px"));
        firstDialogPanel.add(new JBLabel(label4),cc.xy(1,1));
        firstDialogPanel.add(dbUrl,cc.xy(2,1));

        firstDialogPanel.add(new JBLabel(label5),cc.xy(1,2));
        firstDialogPanel.add(dbUserName,cc.xy(2,2));

        firstDialogPanel.add(new JBLabel(label6),cc.xy(1,3));
        firstDialogPanel.add(dbPwd,cc.xy(2,3));
        parent.add(firstDialogPanel);


        JPanel dialogPanel = new DialogPanel(new FormLayout("150px,300px,300px","30px,30px,30px,30px,30px"));
        dialogPanel.add(new JBLabel(label1),cc.xy(1,1));
        dialogPanel.add(xmlRootPath,cc.xy(2,1));
        dialogPanel.add(xmlPackagePath,cc.xy(3,1));

        dialogPanel.add(new JBLabel(label2),cc.xy(1,2));
        dialogPanel.add(entityRootPath,cc.xy(2,2));
        dialogPanel.add(entityPackagePath,cc.xy(3,2));

        dialogPanel.add(new JBLabel(label3),cc.xy(1,3));
        dialogPanel.add(daoRootPath,cc.xy(2,3));
        dialogPanel.add(daoPackagePath,cc.xy(3,3));

        dialogPanel.add(new JBLabel(label7),cc.xy(1,4));
        dialogPanel.add(dtoRootPath,cc.xy(2,4));
        dialogPanel.add(dtoPackagePath,cc.xy(3,4));

        dialogPanel.add(new JBLabel(label8),cc.xy(1,5));
        dialogPanel.add(paramRootPath,cc.xy(2,5));
        dialogPanel.add(paramPackagePath,cc.xy(3,5));


        parent.add(dialogPanel);
        return parent;
    }

    public MySqlConfigBean getConfig(){
        final MySqlConfigBean mySqlConfigBean = new MySqlConfigBean();
        mySqlConfigBean.setDaoPackagePath(daoPackagePath.getText());
        mySqlConfigBean.setDbPwd(dbPwd.getText());
        mySqlConfigBean.setDbUrl(dbUrl.getText());
        mySqlConfigBean.setDbUserName(dbUserName.getText());
        mySqlConfigBean.setMapperPackagePath(xmlPackagePath.getText());
        mySqlConfigBean.setEntityPackagePath(entityPackagePath.getText());
        mySqlConfigBean.setDtoPackagePath(dtoPackagePath.getText());
        mySqlConfigBean.setParamPackagePath(paramPackagePath.getText());

        mySqlConfigBean.setMapperRootPath(xmlRootPath.getText());
        mySqlConfigBean.setEntityRootPath(entityRootPath.getText());
        mySqlConfigBean.setDaoRootPath(daoRootPath.getText());
        mySqlConfigBean.setDtoRootPath(dtoRootPath.getText());
        mySqlConfigBean.setParamRootPath(paramRootPath.getText());
        return mySqlConfigBean;
    }

    public void initDefaultValue(MySqlConfigBean mySqlConfigBean) {
        if (Objects.nonNull(mySqlConfigBean)){
            xmlPackagePath.setText(mySqlConfigBean.getMapperPackagePath());
            entityPackagePath.setText(mySqlConfigBean.getEntityPackagePath());
            daoPackagePath.setText(mySqlConfigBean.getDaoPackagePath());
            dbUrl.setText(mySqlConfigBean.getDbUrl());
            dbUserName.setText(mySqlConfigBean.getDbUserName());
            dbPwd.setText(mySqlConfigBean.getDbPwd());
            dtoPackagePath.setText(mySqlConfigBean.getDtoPackagePath());
            paramPackagePath.setText(mySqlConfigBean.getParamPackagePath());

            xmlRootPath.setText(mySqlConfigBean.getMapperRootPath());
            entityRootPath.setText(mySqlConfigBean.getEntityRootPath());
            daoRootPath.setText(mySqlConfigBean.getDaoRootPath());
            dtoRootPath.setText(mySqlConfigBean.getDtoRootPath());
            paramRootPath.setText(mySqlConfigBean.getParamRootPath());
        }
        init();
    }
}
