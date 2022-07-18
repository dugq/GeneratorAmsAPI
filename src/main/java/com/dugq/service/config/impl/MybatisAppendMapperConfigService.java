package com.dugq.service.config.impl;

import com.dugq.component.mysql.AddMapperConfigDialog;
import com.dugq.exception.StopException;
import com.dugq.pojo.enums.MapperOpEnums;
import com.dugq.pojo.mybatis.AppendMapperConfigBean;
import com.dugq.service.config.AbstractSingleValueConfigService;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author dugq
 * @date 2022/6/29 12:43 上午
 */
public class MybatisAppendMapperConfigService extends AbstractSingleValueConfigService<AppendMapperConfigBean> {
    public static final String mybatisConfigFile = "/mybatisAppendConfig.txt";
    private Project project;

    public MybatisAppendMapperConfigService(Project project) {
        super(project, mybatisConfigFile, AppendMapperConfigBean.class);
        this.project = project;
    }


    public AppendMapperConfigBean getAndFillIfEmpty(List<String> allColumns, List<String> allIndexColumns){
        AppendMapperConfigBean tableConfigBean = read();
        tableConfigBean = fillConfigBean(tableConfigBean,allColumns,allIndexColumns);
        //有必填未填写的，轮训干它
        while (!validate(tableConfigBean)){
            tableConfigBean = fillConfigBean(tableConfigBean,allColumns,allIndexColumns);
        }
        save(tableConfigBean);
        return tableConfigBean;
    }

    private boolean validate(AppendMapperConfigBean tableConfigBean) {
        if (Objects.isNull(tableConfigBean.getOpEnums())){
            return false;
        }
        if (StringUtils.isBlank(tableConfigBean.getMethodName())){
            return false;
        }
        if (tableConfigBean.getOpEnums().equals(MapperOpEnums.INSERT.getType())){
            if (CollectionUtils.isEmpty(tableConfigBean.getInsertColumns())){
                return false;
            }
        }
        else if (tableConfigBean.getOpEnums().equals(MapperOpEnums.UPDATE.getType())){
            if (CollectionUtils.isEmpty(tableConfigBean.getUpdateColumns()) || CollectionUtils.isEmpty(tableConfigBean.getWhereColumns())){
                return false;
            }
        }
        else if (tableConfigBean.getOpEnums().equals(MapperOpEnums.SELECT.getType())){
            if (CollectionUtils.isEmpty(tableConfigBean.getSelectColumns()) || CollectionUtils.isEmpty(tableConfigBean.getWhereColumns())){
                return false;
            }
        }
        else if (tableConfigBean.getOpEnums().equals(MapperOpEnums.DELETE.getType())){
            if (CollectionUtils.isEmpty(tableConfigBean.getWhereColumns())){
                return false;
            }
        }
        return true;
    }

    private AppendMapperConfigBean fillConfigBean(AppendMapperConfigBean mySqlConfigBean, List<String> allColumns, List<String> allIndexColumns) {
        AddMapperConfigDialog initConfigDialog = new AddMapperConfigDialog(project,allColumns,allIndexColumns);
        initConfigDialog.initDefaultValue(mySqlConfigBean);
        if (initConfigDialog.showAndGet()){
            return initConfigDialog.getConfig();
        }
        throw new StopException();
    }

}
