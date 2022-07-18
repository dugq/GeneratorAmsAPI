package com.dugq.component.testapi;

import com.dugq.component.common.MyClickButton;
import com.dugq.pojo.TestApiBean;
import com.dugq.service.config.impl.ApiConfigService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  <h1>api 仓库展示区域</h1>
 *
 *  <h2>功能<h2/>
 * <li>初始化并展示所有API</li>
 * <li>根据uri搜索API</li>
 *
 * <h2>单个API提供：</h2>
 * <li>查看API</li>
 * <li>删除API</li>
 * <li>测试API</li>
 *
 * <h2>实现</h2>
 * <li>每个API都是一个单独的内部组件</li>
 * <li>维护所有组件的列表，内部笑话删除、增加，不对外提供CRUD，需要做出反映时，重新初始化即可</li>
 * <li>每次查询都会重新初始化</li>
 * <li>搜索根据uri模糊匹配，单词contain不做分词匹配</li>
 *
 * @author dugq
 * @date 2021/7/14 2:45 下午
 */
public class ApiRepositoryPanel extends JPanel {

    private Project project;

    private List<ApiLabel> apiBeanList = new ArrayList<>();

    private final ApiConfigService apiConfigService;

    private JTextField select;

    public ApiRepositoryPanel(Project project) {
        this.project = project;
        apiConfigService = ApiConfigService.getInstance(project);
        this.setLayout(new VerticalFlowLayout(0,0,0,true,true));
        this.add(buildSearchPanel());
    }

    @NotNull
    private JPanel buildSearchPanel() {
        final JPanel searchPanel = new JPanel();
        select = new JTextField(50);
        searchPanel.add(select);
        searchPanel.add(new MyClickButton("search",
                (e)-> doInit(apiConfigService.findByUri(select.getText())),
                60));
        return searchPanel;
    }

    public void init() {
        final List<TestApiBean> list = apiConfigService.getList();
        doInit(list);
    }

    private void doInit(List<TestApiBean> list) {
        clear();
        for (TestApiBean testApiBean : list) {
            addApi(new ApiLabel(testApiBean,this,project));
        }
    }

    private void addApi(ApiLabel apiLabel) {
        apiBeanList.add(apiLabel);
        this.add(apiLabel);
        this.updateUI();
    }

    private void removeApi(ApiLabel apiLabel) {
        apiBeanList.remove(apiLabel);
        this.remove(apiLabel);
        this.updateUI();
    }

    public List<ApiLabel> getApiBeanList(){
        return new ArrayList<>(apiBeanList);
    }

    public void clear(){
        for (ApiLabel apiLabel : getApiBeanList()) {
            removeApi(apiLabel);
        }
    }
}
