package com.dugq.service.config.impl;

import com.dugq.component.mysql.AddTableConfigDialog;
import com.dugq.exception.StopException;
import com.dugq.pojo.mybatis.TableConfigBean;
import com.dugq.service.config.AbstractSingleValueConfigService;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dugq
 * @date 2022/6/29 12:43 上午
 */
public class MybatisTableConfigService extends AbstractSingleValueConfigService<TableConfigBean> {
    public static final String mybatisConfigFile = "/mybatisTableConfig.txt";
    private Project project;

    public MybatisTableConfigService(Project project) {
        super(project, mybatisConfigFile, TableConfigBean.class);
        this.project = project;
    }


    public TableConfigBean getAndFillIfEmpty(){
        TableConfigBean tableConfigBean = read();
        tableConfigBean = fillConfigBean(tableConfigBean);
        //有必填未填写的，轮训干它
        while (StringUtils.isAnyBlank(tableConfigBean.getDomain(),tableConfigBean.getTableName())){
            tableConfigBean = fillConfigBean(tableConfigBean);
        }
        save(tableConfigBean);
        return tableConfigBean;
    }

    private TableConfigBean fillConfigBean(TableConfigBean mySqlConfigBean) {
        AddTableConfigDialog initConfigDialog = new AddTableConfigDialog(project);
        initConfigDialog.initDefaultValue(mySqlConfigBean);
        if (initConfigDialog.showAndGet()){
            return initConfigDialog.getConfig();
        }
        throw new StopException();
    }

}
