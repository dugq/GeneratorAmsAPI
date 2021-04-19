package com.dugq.component;

import com.alibaba.fastjson.JSONObject;
import com.dugq.bean.MyJPanelLine;
import com.dugq.service.SaveTestAPIGlobalService;
import com.dugq.service.TestApiService;
import com.dugq.util.JSONPrintUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ScrollPaneFactory;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2021/4/6.
 */
public class TestApiPanel extends SimpleToolWindowPanel implements Disposable {
    public static final String id = "TEST_API";

    private final Project project;
    private final ToolWindow toolWindow;
    private final Splitter splitter;
    private final JTextArea response;
    private JTextField hostFiled;
    private JTextField uriFiled;
    private JComboBox<String> requestMethod = new JComboBox(new String[]{"UNKNOWN","GET","POST"});

    private JPanel customGlobalParamPanel;
    private Map<JTextField,JTextField> globalParamMap = new HashMap<>();

    private JPanel responseAssertPanel;
    private Map<JTextField,JTextField> responseAssertMap = new HashMap<>();

    private JPanel customGlobalHeaderPanel;
    private Map<JTextField,JTextField> headerMap = new HashMap<>();

    private JTextArea requestParamArea;

    public TestApiPanel(Project p, ToolWindow t) {
        super(true, true);
        this.project = p;
        this.toolWindow = t;
        this.splitter = new Splitter(false, 0.65f,0.5f,0.7f);
        this.response = new JTextArea();
        this.response.addMouseListener(new KjjMenu(response));
        this.response.setEditable(true);
        setContent(splitter);
        Splitter choosePanel = buildLeftChoosePanel();
        this.splitter.setFirstComponent(choosePanel);
        this.splitter.setSecondComponent(ScrollPaneFactory.createScrollPane(response));
        this.setToolbar(buildToolbar(project));
    }

    @NotNull
    private JPanel buildToolbar(Project project) {
        JPanel toolbarPanel = new JPanel();
        JButton addHeader = buildAddHeader();
        toolbarPanel.add(addHeader);

        JButton addParam = buildAddGlobalParam();
        toolbarPanel.add(addParam);

        JButton response = buildAddResponseAssert();
        toolbarPanel.add(response);

        JButton startTest = buildStartButton(project);
        toolbarPanel.add(startTest);
        return toolbarPanel;
    }

    private JButton buildStartButton(Project project) {
        JButton header = new JButton("|>");
        header.setToolTipText("发起请求");
        header.setPreferredSize(new Dimension(30,30));
        header.setForeground(Color.BLUE);
        header.addMouseListener(new MyClickListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    if (StringUtils.isBlank(getUri())){
                        setResponse("请先选择方法！");
                        return;
                    }
                    new Thread(() -> {
                        TestApiService service = project.getService(TestApiService.class);
                        service.sendCurrentRequest();
                    }).start();

                }
            }
        });
        return header;
    }

    private JButton buildAddHeader() {
        JButton header = new JButton("H+");
        header.setToolTipText("添加全局header");
        header.setPreferredSize(new Dimension(30,30));
        header.setForeground(Color.BLUE);
        header.addMouseListener(new MyClickListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    new MyJPanelLine(customGlobalHeaderPanel,headerMap);
                }
            }
        });
        return header;
    }

    @NotNull
    private JButton buildAddResponseAssert() {
        JButton response = new JButton("R+");
        response.setToolTipText("添加返回值断言");
        response.setForeground(Color.CYAN);
        response.setPreferredSize(new Dimension(30,30));
        response.addMouseListener(new MyClickListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    new MyJPanelLine(responseAssertPanel,responseAssertMap);
                }
            }
        });
        return response;
    }

    @NotNull
    private JButton buildAddGlobalParam() {
        JButton addParam = new JButton("P+");
        response.setToolTipText("添加全局参数值");
        addParam.setPreferredSize(new Dimension(30,30));
        addParam.setForeground(Color.GREEN);
        addParam.addMouseListener(new MyClickListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton()==1){
                    new MyJPanelLine(customGlobalParamPanel,globalParamMap);
                }
            }
        });
        return addParam;
    }

    private Splitter buildLeftChoosePanel() {
        Splitter choosePanel = new Splitter(true, 0.3f,0.2f,0.6f);
        choosePanel.setFirstComponent(buildGlobalPanel());
        choosePanel.setSecondComponent(buildRequestPanel());
        return choosePanel;
    }

    private Splitter buildGlobalPanel(){
        Splitter globalPanel= new Splitter(true, 0.05f);
        JPanel hostPanel = new JPanel();
        this.hostFiled = new JTextField("",20);
        hostPanel.add(new JLabel("HOST"),0);
        hostPanel.add(this.hostFiled);
        globalPanel.setFirstComponent(hostPanel);
        globalPanel.setSecondComponent(builderGlobalParamPanel());
        return globalPanel;
    }

    private Splitter builderGlobalParamPanel() {
        Splitter globalParamPanel= new Splitter(false, 0.5f);

        VerticalFlowLayout mgr = new VerticalFlowLayout(0,0,0,true,false);
        customGlobalHeaderPanel = new JPanel();
        customGlobalHeaderPanel.setLayout(mgr);
        globalParamPanel.setFirstComponent(ScrollPaneFactory.createScrollPane(customGlobalHeaderPanel));


        customGlobalParamPanel = new JPanel();
        customGlobalParamPanel.setLayout(mgr);
        globalParamPanel.setSecondComponent(ScrollPaneFactory.createScrollPane(customGlobalParamPanel));
        return globalParamPanel;
    }

    private Splitter buildRequestPanel(){
        Splitter requestPanel= new Splitter(true, 0.05f);
        JPanel urlPanel = new JPanel();
        this.uriFiled = new JTextField("",40);
        urlPanel.add(requestMethod,0);
        urlPanel.add(new JLabel("URI"),1);
        urlPanel.add(this.uriFiled,2);
        requestPanel.setFirstComponent(urlPanel);
        requestPanel.setSecondComponent(buildParamResultPanel());
        return requestPanel;
    }

    private Splitter buildParamResultPanel() {
        Splitter paramResult = new Splitter(false,0.5f,0.5f,0.5f);
        paramResult.setFirstComponent(ScrollPaneFactory.createScrollPane(buildRequestParamPanel()));
        paramResult.setSecondComponent(ScrollPaneFactory.createScrollPane(buildResponsePanel()));
        return paramResult;
    }

    private JTextArea buildRequestParamPanel() {
        return this.requestParamArea = new JTextArea();
    }

    private JPanel buildResponsePanel() {
        this.responseAssertPanel = new JPanel();
        VerticalFlowLayout mgr = new VerticalFlowLayout(0,0,0,true,false);
        responseAssertPanel.setLayout(mgr);
        return this.responseAssertPanel;
    }


    public void addGlobalParam(String key,String value){
        MyJPanelLine.addKeyValue(key,value,customGlobalParamPanel,globalParamMap);
    }

    public Map<String,String> getGlobalParamMap(){
        return MyJPanelLine.getAllKeyValueMap(globalParamMap);
    }

    public void addHeader(String key,String value){
        MyJPanelLine.addKeyValue(key,value,customGlobalHeaderPanel,headerMap);
    }

    public Map<String,String> getHeaderMap(){
        return  MyJPanelLine.getAllKeyValueMap(headerMap);
    }

    public void addResponseAssert(String key,String value){
        MyJPanelLine.addKeyValue(key,value,responseAssertPanel,responseAssertMap);
    }

    public Map<String,String> getResponseAssertMap(){
        return  MyJPanelLine.getAllKeyValueMap(responseAssertMap);
    }

    public void setHost(String host){
        this.hostFiled.setText(host);
    }

    public String getHost(){
        return this.hostFiled.getText();
    }

    public void setUri(String uri){
        this.uriFiled.setText(uri);
    }

    public String getUri(){
        return this.uriFiled.getText();
    }

    public void clearAndSetParam(String param){
        JSONPrintUtils.printJson(param, requestParamArea);
    }

    public void clearAndSetParam(JSONObject param){
        requestParamArea.setText("");
        JSONPrintUtils.printJson(param, requestParamArea);
    }

    public String getRequestParam(){
        return requestParamArea.getText();
    }

    public void setRequestMethod(String httpMethod){
        requestMethod.setSelectedItem(httpMethod);
    }

    public String getRequestMethod(){
        return (String)requestMethod.getSelectedItem();
    }


    public void setResponse(String response){
        this.response.append(response);
    }


    public void printResponse(String responseBody) {
        JSONPrintUtils.printJson(responseBody, response);
    }


    @Override
    public void dispose() {
        SaveTestAPIGlobalService.save(project);
    }
}
