package com.dugq.component.testapi;

import com.dugq.component.common.MyClickListener;
import com.dugq.component.tool.KeyValueDescPanel;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.TestApiBean;
import com.dugq.service.config.impl.GlobalHeadersConfigService;
import com.dugq.service.config.impl.GlobalParamConfigService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBEditorTabs;
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
    private final JBEditorTabs apiTabbedPane;
    private final KeyValueDescPanel headerPanel;
    private final KeyValueDescPanel globalParamPanel;
    private final ApiRepositoryPanel apiRepositoryPanel;
    //uri - api
    private final Map<String,TabInfo> apiMap = new HashMap<>();

    public static final String EMPTY_PANEL_FLAG = "empty";

    public TestApiPanel(Project project, ToolWindow t) {
        super(true, true);
        this.project = project;
        this.toolWindow = t;
        apiTabbedPane =new JBEditorTabs(project,null,this);
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
                    testApiPanel.showHeaders();
                }
            }
        });

        JButton globalParamButton = new JButton("全局参数");
        globalParamButton.addMouseListener(new MyClickListener(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    testApiPanel.showGlobalParam();
                }
            }
        });

        JButton api = new JButton("api");
        api.addMouseListener(new MyClickListener(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    testApiPanel.showApi();
                }
            }
        });

        JButton repository = new JButton("repository");
        repository.addMouseListener(new MyClickListener(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    testApiPanel.showRepository();
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

    public void showHeaders() {
        setContent(headerPanel);
    }

    public void showApi() {
        setContent(apiTabbedPane);
    }

    public void showGlobalParam() {
        setContent(globalParamPanel);
    }

    public void showRepository() {
        setContent(apiRepositoryPanel);
    }

    /**
     * 优先从也存在的选项卡中显示出来
     * @param apiBean
     */
    public void testApi(ApiBean apiBean){
        final String apiURI = apiBean.getApiURI();
        if (apiMap.containsKey(apiURI)){
            final TabInfo mainPanel = apiMap.get(apiURI);
            apiTabbedPane.select(mainPanel,true);
            return;
        }
        MainPanel mainContent = createNewMainPanelAndSelect(apiURI, apiBean.getApiName());
        mainContent.showTestApi(apiBean);
        setContent(apiTabbedPane);
        this.updateUI();
    }

    public MainPanel createNewMainPanelAndSelect(String apiURI, String apiName) {
        final TabInfo tabInfo = apiMap.remove(EMPTY_PANEL_FLAG);
        if (Objects.nonNull(tabInfo)){
            tabInfo.setText(apiName);
            apiMap.put(apiURI,tabInfo);
            return (MainPanel)tabInfo.getComponent();
        }
        final MainPanel mainContent = new MainPanel(project, this);
        final TabInfo info = new TabInfo(mainContent);
        DefaultActionGroup tabActionGroup = new DefaultActionGroup();
        tabActionGroup.add(new AnAction("Close tabs", "Click to close tab", AllIcons.Actions.CloseHovered) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                final MainPanel component = (MainPanel) info.getComponent();
                final String uri = component.getUri();
                apiMap.remove(uri);
                apiTabbedPane.removeTab(info);
            }
        });
        info.setTabLabelActions(tabActionGroup, ActionPlaces.EDITOR_TAB);
        info.setText(apiName);
        apiTabbedPane.addTab(info);
        apiMap.put(apiURI, info);
        return mainContent;
    }

    public void testApi(TestApiBean apiBean){
        if (apiMap.containsKey(apiBean.getUri())){
            final TabInfo mainPanel = apiMap.get(apiBean.getUri());
            apiTabbedPane.select(mainPanel,true);
            return;
        }
        MainPanel mainContent = createNewMainPanelAndSelect(apiBean.getUri(), apiBean.getName());
        mainContent.showTestApi(apiBean);
        setContent(apiTabbedPane);
        this.updateUI();
    }


    @Override
    public void dispose() {
    }

    public void clearResponse(){
        final MainPanel mainContent = getOrCreateSelectedMainContent();
        if(Objects.nonNull(mainContent)){
            mainContent.clearResponse();
        }
    }

    public void createNewEmptyMainPanelAndSelect(){
        final TabInfo tabInfo = apiMap.get(EMPTY_PANEL_FLAG);
        if (Objects.isNull(tabInfo)){
            createNewMainPanelAndSelect(EMPTY_PANEL_FLAG, EMPTY_PANEL_FLAG);
        }else{
            apiTabbedPane.select(tabInfo,true);
        }
    }

    public MainPanel getOrCreateSelectedMainContent(){
        final TabInfo selectedInfo = apiTabbedPane.getSelectedInfo();
        if (Objects.isNull(selectedInfo)){
            return createNewMainPanelAndSelect(EMPTY_PANEL_FLAG,EMPTY_PANEL_FLAG);
        }
        return (MainPanel) selectedInfo.getComponent();
    }

    public Map<String, String> getHeaders() {
        return this.headerPanel.getAllKeyValue();
    }

    public Map<String, String> getGlobalParamMap() {
        return this.globalParamPanel.getAllKeyValue();
    }

    public void printError(String errorMsg) {
        MainPanel mainContent = getOrCreateSelectedMainContent();
        mainContent.printError(errorMsg);
    }

}
