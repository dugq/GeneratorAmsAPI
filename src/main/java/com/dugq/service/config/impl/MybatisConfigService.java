package com.dugq.service.config.impl;

import com.dugq.component.mysql.InitConfigDialog;
import com.dugq.exception.StopException;
import com.dugq.pojo.mybatis.MySqlConfigBean;
import com.dugq.service.config.AbstractSingleValueConfigService;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author dugq
 * @date 2022/6/29 12:43 上午
 */
public class MybatisConfigService extends AbstractSingleValueConfigService<MySqlConfigBean> {
    public static final String mybatisConfigFile = "/mybatisGenerator.txt";
    private Project project;

    public MybatisConfigService(Project project) {
        super(project, mybatisConfigFile, MySqlConfigBean.class);
        this.project = project;
    }


    public MySqlConfigBean getAndFillIfEmpty(){
        MySqlConfigBean mySqlConfigBean = read();
        mySqlConfigBean = fillConfigBean(mySqlConfigBean);
        //有必填未填写的，轮训干它
        while (Objects.isNull(mySqlConfigBean) ||
                StringUtils.isAnyBlank(
                        mySqlConfigBean.getDaoPackagePath(),
                        mySqlConfigBean.getDbPwd(),
                        mySqlConfigBean.getDbUrl(),
                        mySqlConfigBean.getDbUserName(),
                        mySqlConfigBean.getMapperPackagePath(),
                        mySqlConfigBean.getDtoPackagePath(),
                        mySqlConfigBean.getParamPackagePath(),
                        mySqlConfigBean.getMapperPackagePath(),
                        mySqlConfigBean.getDaoRootPath(),
                        mySqlConfigBean.getMapperRootPath(),
                        mySqlConfigBean.getEntityRootPath(),
                        mySqlConfigBean.getDtoRootPath(),
                        mySqlConfigBean.getParamRootPath()
                )){
            mySqlConfigBean = fillConfigBean(mySqlConfigBean);
        }
        save(mySqlConfigBean);
        return mySqlConfigBean;
    }

    private MySqlConfigBean fillConfigBean(MySqlConfigBean mySqlConfigBean) {
        InitConfigDialog initConfigDialog = new InitConfigDialog(project);
        initConfigDialog.initDefaultValue(mySqlConfigBean);
        if (initConfigDialog.showAndGet()){
            return initConfigDialog.getConfig();
        }
        throw new StopException();
    }

}
