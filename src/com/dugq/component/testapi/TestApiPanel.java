package com.dugq.component.testapi;

import com.dugq.component.tool.KeyValueDescPanel;
import com.dugq.component.common.MyClickListener;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.TestApiBean;
import com.dugq.service.config.impl.ApiConfigService;
import com.dugq.service.config.impl.GlobalHeadersConfigService;
import com.dugq.service.config.impl.GlobalParamConfigService;
import com.dugq.util.TestApiUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dugq on 2021/4/6.
 */
public class TestApiPanel extends SimpleToolWindowPanel implements Disposable {

    //---parents---
    private final Project project;
    private final ToolWindow toolWindow;

    //-----children-------
    private final MainPanel mainContent;
    private final KeyValueDescPanel headerPanel;
    private final KeyValueDescPanel globalParamPanel;
    private final ApiRepositoryPanel apiRepositoryPanel;

    public TestApiPanel(Project project, ToolWindow t) {
        super(true, true);
        this.project = project;
        this.toolWindow = t;
        this.mainContent = new MainPanel(project,this);
        this.headerPanel = new KeyValueDescPanel(project,GlobalHeadersConfigService.class);
        this.globalParamPanel = new KeyValueDescPanel(project,GlobalParamConfigService.class);
        this.apiRepositoryPanel = new ApiRepositoryPanel(project);
        setContent(mainContent);
        this.setToolbar(buildToolbar(this));
    }

    @NotNull
    private JPanel buildToolbar(TestApiPanel testApiPanel) {
        JPanel toolbarPanel = new JPanel();
        JButton headerButton = new JButton("headers");
        headerButton.addMouseListener(new MyClickListener(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    testApiPanel.setContent(headerPanel);
                }
            }
        });

        JButton globalParamButton = new JButton("全局参数");
        globalParamButton.addMouseListener(new MyClickListener(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    testApiPanel.setContent(globalParamPanel);
                }
            }
        });

        JButton api = new JButton("api");
        api.addMouseListener(new MyClickListener(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    testApiPanel.setContent(mainContent);
                }
            }
        });

        JButton repository = new JButton("repository");
        repository.addMouseListener(new MyClickListener(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    testApiPanel.setContent(apiRepositoryPanel);
                    apiRepositoryPanel.init();
                }
            }
        });

        toolbarPanel.add(api);
        toolbarPanel.add(headerButton);
        toolbarPanel.add(globalParamButton);
        toolbarPanel.add(repository);
        return toolbarPanel;
    }


    public void testApi(ApiBean apiBean){
        final TestApiBean testApiBean = ApiConfigService.getInstance(project).findOneByUri(apiBean.getApiURI());
        if (Objects.nonNull(testApiBean)){
            testApi(testApiBean);
        }else{
            this.mainContent.showTestApi(apiBean);
            this.setContent(this.mainContent);
            this.updateUI();
        }
        TestApiUtil.show(project);
    }

    public void testApi(TestApiBean apiBean){
        this.mainContent.showTestApi(apiBean);
        this.setContent(this.mainContent);
        this.updateUI();
    }


    @Override
    public void dispose() {
    }

    public void clearResponse(){
        this.mainContent.clearResponse();
    }

    public MainPanel getMainContent(){
        return this.mainContent;
    }

    public Map<String, String> getHeaders() {
        return this.headerPanel.getAllKeyValue();
    }

    public Map<String, String> getGlobalParamMap() {
        return this.globalParamPanel.getAllKeyValue();
    }

    public void printError(String errorMsg) {
        this.mainContent.printError(errorMsg);
    }
}
