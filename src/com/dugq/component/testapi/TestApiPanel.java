package com.dugq.component.testapi;

import com.dugq.component.common.MyClickListener;
import com.dugq.component.tool.KeyValueDescPanel;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.TestApiBean;
import com.dugq.service.config.impl.GlobalHeadersConfigService;
import com.dugq.service.config.impl.GlobalParamConfigService;
import com.dugq.util.TestApiUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.profiler.ui.JBRunnerClosableTabs;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
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
    private final JBRunnerClosableTabs apiTabbedPane;
    private final KeyValueDescPanel headerPanel;
    private final KeyValueDescPanel globalParamPanel;
    private final ApiRepositoryPanel apiRepositoryPanel;
    //uri - api
    private final Map<String,TabInfo> apiMap = new HashMap<>();

    public TestApiPanel(Project project, ToolWindow t) {
        super(true, true);
        this.project = project;
        this.toolWindow = t;
        apiTabbedPane =new JBRunnerClosableTabs(project,this);
        this.headerPanel = new KeyValueDescPanel(project,GlobalHeadersConfigService.class);
        this.globalParamPanel = new KeyValueDescPanel(project,GlobalParamConfigService.class);
        this.apiRepositoryPanel = new ApiRepositoryPanel(project);
        setContent(apiTabbedPane);
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
                    testApiPanel.setContent(apiTabbedPane);
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

    /**
     * 优先从也存在的选项卡中显示出来
     * @param apiBean
     */
    public void testApi(ApiBean apiBean){
        if (apiMap.containsKey(apiBean.getApiURI())){
            final TabInfo mainPanel = apiMap.get(apiBean.getApiURI());
            apiTabbedPane.select(mainPanel,true);
            return;
        }
        final MainPanel mainContent = new MainPanel(project, this);
        final TabInfo info = new TabInfo(mainContent);
        info.setText(apiBean.getApiName());
        apiTabbedPane.addClosableTab(info,true);
        apiTabbedPane.addListener(new TabsListener(){
            @Override
            public void tabRemoved(@NotNull TabInfo tabToRemove) {
                final MainPanel component = (MainPanel)tabToRemove.getComponent();
                final String uri = component.getUri();
                apiMap.remove(uri);
            }
        });
        apiMap.put(apiBean.getApiURI(),info);
        mainContent.showTestApi(apiBean);
        setContent(apiTabbedPane);
        this.updateUI();
        TestApiUtil.show(project);
    }

    public void testApi(TestApiBean apiBean){
        if (apiMap.containsKey(apiBean.getUri())){
            final TabInfo mainPanel = apiMap.get(apiBean.getUri());
            apiTabbedPane.select(mainPanel,true);
            return;
        }
        final MainPanel mainContent = new MainPanel(project, this);
        final TabInfo info = new TabInfo(mainContent);
        info.setText(apiBean.getName());
        apiTabbedPane.addClosableTab(info,true);
        apiTabbedPane.addListener(new TabsListener(){
            @Override
            public void tabRemoved(@NotNull TabInfo tabToRemove) {
                final MainPanel component = (MainPanel)tabToRemove.getComponent();
                final String uri = component.getUri();
                apiMap.remove(uri);
            }
        });
        apiMap.put(apiBean.getUri(),info);
        mainContent.showTestApi(apiBean);
        setContent(apiTabbedPane);
        this.updateUI();
        TestApiUtil.show(project);
    }


    @Override
    public void dispose() {
    }

    public void clearResponse(){
        final MainPanel mainContent = getMainContent();
        if(Objects.nonNull(mainContent)){
            mainContent.clearResponse();
        }
    }

    public MainPanel getMainContent(){
        final TabInfo selectedInfo = apiTabbedPane.getSelectedInfo();
        if (Objects.nonNull(selectedInfo)){
            return (MainPanel)selectedInfo.getComponent();
        }
        return new MainPanel(project,this);
    }

    public Map<String, String> getHeaders() {
        return this.headerPanel.getAllKeyValue();
    }

    public Map<String, String> getGlobalParamMap() {
        return this.globalParamPanel.getAllKeyValue();
    }

    public void printError(String errorMsg) {
        MainPanel mainContent = getMainContent();
        if (Objects.isNull(mainContent)){
            mainContent = new MainPanel(project,this);
        }
        mainContent.printError(errorMsg);
    }

}
