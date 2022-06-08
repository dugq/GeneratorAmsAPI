package com.dugq.component.testapi;

import com.dugq.component.common.MyClickListener;
import com.dugq.component.common.NotifyComponent;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.TestApiBean;
import com.dugq.pojo.TestApiParamBean;
import com.dugq.pojo.enums.RequestType;
import com.dugq.service.TestApiService;
import com.dugq.service.config.impl.ApiConfigService;
import com.dugq.util.FileUtil;
import com.dugq.util.JSONPrintUtils;
import com.dugq.util.ParamBeanUtils;
import com.dugq.util.TestApiUtil;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithStoredHistory;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

/**
 * @author dugq
 * @date 2021/7/8 11:22 上午
 */
public class MainPanel extends JBSplitter {

    private TestApiPanel parent;
    private Project project;
    private final ConsoleViewImpl responseArea;
    private TextFieldWithHistory hostFiled;
    private JTextField uriFiled;
    private JComboBox<String> requestMethod = new ComboBox(new String[]{"UNKNOWN","GET","POST"});
    private ParameterPanel parameterPanel;

    private TestApiBean currentTestApi;

    public MainPanel(Project project, TestApiPanel parent) {
        super(true, 0.01f,0.01f,0.02f);
        this.project = project;
        this.parent = parent;
        this.responseArea = (ConsoleViewImpl) TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        this.parameterPanel = new ParameterPanel(project);
        this.setFirstComponent(buildUrlPanel(project));
        this.setSecondComponent(buildParamPanel());
    }

    /**
     * 调试新的API
     */
    public void showTestApi(ApiBean targetApi){
        this.currentTestApi = changeTestApi2Bean(targetApi);
        setUri(targetApi.getApiURI());
        setRequestMethod(targetApi.getApiRequestType().getDesc().toUpperCase());
        setDefaultHost();
        this.parameterPanel.init(currentTestApi.getApiParamBean());
    }

    public void showTestApi(TestApiBean testApiBean){
        this.resetContent();
        this.currentTestApi = testApiBean;
        setUri(currentTestApi.getUri());
        setRequestMethod(currentTestApi.getRequestType().getDesc().toUpperCase());
        setHost(testApiBean.getHost());
        this.parameterPanel.init(currentTestApi.getApiParamBean());
    }

    private TestApiBean changeTestApi2Bean(ApiBean testApi) {
        TestApiBean testApiBean = new TestApiBean();
        testApiBean.setRequestType(testApi.getApiRequestType());
        if (testApi.isRpc()){
            TestApiParamBean testApiParamBean = new TestApiParamBean();
            final List<ParamBean> apiParamBean = testApi.getFeignParamBean();
            testApiParamBean.setFeignKeyValueBeans(ParamBeanUtils.param2KVBean(apiParamBean));
            testApiParamBean.setModel(TestApiParamBean.FEIGN_JSON_MODEL);
            testApiBean.setApiParamBean(testApiParamBean);
        }else if (Objects.equals(testApi.getApiRequestType(),RequestType.get)){
            TestApiParamBean testApiParamBean = new TestApiParamBean();
            final List<ParamBean> apiParamBean = testApi.getApiParamBean();
            testApiParamBean.setRequestParams(ParamBeanUtils.param2KVBean(apiParamBean,true,null));
            testApiParamBean.setModel(TestApiParamBean.PARAM_MAP_MODEL);
            testApiBean.setApiParamBean(testApiParamBean);
        }else if (Objects.equals(testApi.getApiRequestType(),RequestType.post)){
            TestApiParamBean testApiParamBean = new TestApiParamBean();
            testApiParamBean.setRequestBody(ParamBeanUtils.param2Json(testApi.getApiParamBean(),true).toJSONString());
            testApiParamBean.setModel(TestApiParamBean.BODY_JSON_MODEL);
            testApiBean.setApiParamBean(testApiParamBean);
        }else{
            throw new ErrorException("not support request type "+testApi.getApiRequestType());
        }
        testApiBean.setUri(testApi.getApiURI());
        testApiBean.setName(testApi.getApiName());
        return testApiBean;
    }

    public TestApiBean getTestApi(){
        if (Objects.isNull(currentTestApi)){
            currentTestApi = new TestApiBean();
            currentTestApi.setName(getUri());
        }
        currentTestApi.setUri(getUri());
        currentTestApi.setApiParamBean(parameterPanel.getContent());
        currentTestApi.setRequestType(RequestType.getByDesc(getRequestMethod()));
        currentTestApi.setHost(getHost());
        return currentTestApi;
    }




    public void setUri(String uri){
        this.uriFiled.setText(uri);
    }

    public String getUri(){
        return this.uriFiled.getText();
    }


    public void setRequestMethod(String httpMethod){
        requestMethod.setSelectedItem(httpMethod.toUpperCase());
    }

    public String getRequestMethod(){
        return (String)requestMethod.getSelectedItem();
    }

    public void clearAndPrintResponse(String responseBody) {
        clearResponse();
        JSONPrintUtils.printCustomJson(responseBody, responseArea);
    }

    public void setHost(String host){
        this.hostFiled.setTextAndAddToHistory(host);
    }

    public void setDefaultHost(){
        final String defaultPort = FileUtil.getDefaultPort(project);
        if (StringUtils.isBlank(this.hostFiled.getText())){
            this.hostFiled.setTextAndAddToHistory("http://127.0.0.1:"+defaultPort);
        }
    }

    public String getHost(){
        this.hostFiled.addCurrentTextToHistory();
        return this.hostFiled.getText();
    }




    private JPanel buildUrlPanel(Project project) {
        JPanel jPanel = new JPanel();
        jPanel.add(buildHostPanel());
        jPanel.add(requestMethod);
        jPanel.add(buildRequestUriPanel());
        jPanel.add(buildStartButton(project));
        jPanel.add(buildSaveButton(project));
        return jPanel;
    }

    private Splitter buildHostPanel(){
        Splitter globalPanel= new Splitter(true, 0.05f);
        JPanel hostPanel = new JPanel();
        this.hostFiled = new TextFieldWithStoredHistory("api-host");
        Dimension dimension = new Dimension();
        dimension.setSize(400,10);
        this.hostFiled.setSize(dimension);
        hostPanel.add(new JLabel("HOST"),0);
        hostPanel.add(this.hostFiled);
        globalPanel.setFirstComponent(hostPanel);
        return globalPanel;
    }


    private Splitter buildParamPanel() {
        Splitter paramResult = new JBSplitter(false,0.5f);
        final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(this.parameterPanel);
        paramResult.setFirstComponent(scrollPane);
        paramResult.setSecondComponent(this.responseArea.getComponent());
        return paramResult;
    }

    private JPanel buildRequestUriPanel(){
        JPanel urlPanel = new JPanel();
        this.uriFiled = new JTextField("",40);
        urlPanel.add(requestMethod,0);
        urlPanel.add(new JLabel("URI"),1);
        urlPanel.add(this.uriFiled,2);
        return urlPanel;
    }

    private JButton buildStartButton(Project project) {
        JButton send = new JButton();
        send.setText("发送");
        send.setToolTipText("发起请求");
        send.setPreferredSize(new Dimension(80,30));
        send.addMouseListener(new MyClickListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    if (StringUtils.isBlank(getUri())){
                        NotifyComponent.error("请先选择API！",project);
                        return;
                    }
                    TestApiService service = project.getService(TestApiService.class);
                    service.sendCurrentRequest(getTestApi(), parent);
                }
            }
        });
        return send;
    }

    private JButton buildSaveButton(Project project) {
        JButton saveButton = new JButton("保存");
        saveButton.setToolTipText("保存");
        saveButton.setPreferredSize(new Dimension(80,30));
        saveButton.addMouseListener(new MyClickListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    if (Objects.isNull(currentTestApi)){
                        NotifyComponent.error("请先选择API！",project);
                        return;
                    }
                    try{
                        ApiConfigService.getInstance(project).save(getTestApi());
                    }catch (Exception ex){
                        TestApiUtil.printException(ex,project);
                    }
                }
            }
        });
        return saveButton;
    }

    public void printError(String errorMsg) {
        this.responseArea.print(errorMsg, ConsoleViewContentType.ERROR_OUTPUT);
    }

    public void clearResponse() {
        this.responseArea.clear();
    }

    public void resetContent() {
        clearResponse();
        this.setUri("");
        this.parameterPanel.resetContent();
    }
}
