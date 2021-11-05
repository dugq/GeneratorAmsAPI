package com.dugq.component.testapi;

import com.dugq.component.common.MyClickButton;
import com.dugq.pojo.TestApiParamBean;
import com.dugq.util.TestApiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.ScrollPaneFactory;

import javax.swing.*;
import java.util.Objects;

/**
 * @author dugq
 * @date 2021/7/8 5:36 下午
 */
public class ParameterPanel extends Splitter {

    private final Project project;

    private final ParameterKeyValuePanel keyValueContent;

    private final JSONPanel jsonView;

    private final FeignParameterPanel feignParameterPanel;
    /**
     * 1: params 2: json
     */
    private volatile Integer currentModel;

    public ParameterPanel(Project project) {
        super(true,0.1f);
        this.project = project;
        this.keyValueContent = new ParameterKeyValuePanel(project);
        this.feignParameterPanel = new FeignParameterPanel(project);
        this.jsonView = new JSONPanel(project);
        this.setFirstComponent(buildBoorPanel());
        setSecondComponent(ScrollPaneFactory.createScrollPane(this.keyValueContent));
    }

    private JPanel buildBoorPanel() {
        JPanel boor = new JPanel();
        boor.add(new MyClickButton("paramMap",(e)-> {
            try{
                changeModel(TestApiParamBean.PARAM_MAP_MODEL);
            }catch (Exception ex){
                TestApiUtil.printException(ex,project);
            }
        },80));
        boor.add(new MyClickButton("body",(e)-> {
            try{
                changeModel(TestApiParamBean.BODY_JSON_MODEL);
            }catch (Exception ex){
                TestApiUtil.printException(ex,project);
            }
        },80));
        boor.add(new MyClickButton("feign",(e)-> {
            try{
                changeModel(TestApiParamBean.FEIGN_JSON_MODEL);
            }catch (Exception ex){
                TestApiUtil.printException(ex,project);
            }
        },80));
        return boor;
    }

    private synchronized void changeModel(Integer targetModel){
        if (Objects.equals(currentModel, targetModel)){
            this.updateUI();
            return;
        }
        if (Objects.equals(targetModel, TestApiParamBean.BODY_JSON_MODEL)){
            setSecondComponent(jsonView);
        }else if(Objects.equals(targetModel, TestApiParamBean.PARAM_MAP_MODEL)){
            setSecondComponent(ScrollPaneFactory.createScrollPane(this.keyValueContent));
        }else{
            setSecondComponent(feignParameterPanel);
        }
        currentModel = targetModel;
        this.updateUI();
    }

    public void init(TestApiParamBean testApiParamBean){
        if (Objects.isNull(testApiParamBean)){
            return;
        }
        this.keyValueContent.init(testApiParamBean.getRequestParams());
        this.jsonView.init(testApiParamBean.getRequestBody());
        this.feignParameterPanel.init(testApiParamBean.getFeignKeyValueBeans());
        changeModel(testApiParamBean.getModel());
        this.updateUI();
    }

    public void resetContent() {
        this.keyValueContent.clear();
        this.jsonView.clear();
    }

    public TestApiParamBean getContent() {
        TestApiParamBean testApiParamBean = new TestApiParamBean();
        testApiParamBean.setRequestBody(this.jsonView.getContent());
        testApiParamBean.setRequestParams(this.keyValueContent.getContent());
        testApiParamBean.setFeignKeyValueBeans(this.feignParameterPanel.getContent());
        testApiParamBean.setModel(this.currentModel);
        return testApiParamBean;
    }

}
