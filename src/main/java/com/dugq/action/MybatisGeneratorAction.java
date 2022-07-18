package com.dugq.action;

import com.dugq.component.common.NotifyComponent;
import com.dugq.exception.SqlException;
import com.dugq.exception.StopException;
import com.dugq.mybatisgenerator.generator3.MyGenerator;
import com.dugq.pojo.mybatis.TableConfigBean;
import com.dugq.service.config.impl.MybatisTableConfigService;
import com.dugq.util.APIPrintUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author dugq
 * @date 2022/6/29 12:58 上午
 */
public class MybatisGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        if (Objects.isNull(project)) {
            NotifyComponent.error("找不到project", project);
            return;
        }
        try {
            final MyGenerator myGenerator = project.getService(MyGenerator.class);
            myGenerator.init();
            final MybatisTableConfigService tableConfigService = project.getService(MybatisTableConfigService.class);
            final TableConfigBean config = tableConfigService.getAndFillIfEmpty();
            myGenerator.addTable(config);
            myGenerator.generator();
        } catch (SqlException sqlException) {
            sqlException.getWarns().forEach(ex -> APIPrintUtil.printErrorLine(ex, project));
            sqlException.printStackTrace();
        } catch (StopException stopException) {
            //do nothing
        } catch (Exception ex) {
            APIPrintUtil.printErrorLine(ex.getMessage(), project);
            ex.printStackTrace();
        }


    }
}
