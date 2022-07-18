package com.dugq.action;

import com.dugq.component.common.NotifyComponent;
import com.dugq.exception.SqlException;
import com.dugq.exception.StopException;
import com.dugq.mybatisgenerator.generator3.MyAppendMapperGenerator;
import com.dugq.pojo.mybatis.AppendMapperConfigBean;
import com.dugq.pojo.mybatis.TableConfigBean;
import com.dugq.service.config.impl.MybatisAppendMapperConfigService;
import com.dugq.service.config.impl.MybatisTableConfigService;
import com.dugq.util.APIPrintUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author dugq
 * @date 2022/6/29 12:58 上午
 */
public class MybatisAddMapperAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        if (Objects.isNull(project)){
            NotifyComponent.error("找不到project",project);
            return;
        }
        try {
            final MyAppendMapperGenerator myGenerator = project.getService(MyAppendMapperGenerator.class);
            myGenerator.initBaseConfig();
            final MybatisTableConfigService tableConfigService = project.getService(MybatisTableConfigService.class);
            final TableConfigBean config = tableConfigService.getAndFillIfEmpty();
            myGenerator.addTable(config);
            MybatisAppendMapperConfigService mybatisAppendMapperConfigService = project.getService(MybatisAppendMapperConfigService.class);
            final List<String> allColumns = myGenerator.getAllColumns();
            final List<String> allIndexColumns = myGenerator.getAllIndexColumns();
            final AppendMapperConfigBean appendConfig = mybatisAppendMapperConfigService.getAndFillIfEmpty(allColumns,allIndexColumns);
            myGenerator.generator(appendConfig);
        }catch (SqlException sqlException){
            sqlException.printStackTrace();
            sqlException.getWarns().forEach(ex-> APIPrintUtil.printErrorLine(ex,project));
        }catch (StopException stopException){
             //do nothing
            stopException.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
            APIPrintUtil.printErrorLine(ex.getMessage(),project);
        }


    }
}
