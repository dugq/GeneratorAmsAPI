package com.dugq.component.testapi;

import com.dugq.component.common.MyClickButton;
import com.dugq.pojo.TestApiBean;
import com.dugq.service.config.impl.ApiConfigService;
import com.dugq.util.TestApiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @author dugq
 * @date 2021/7/14 3:09 下午
 */
public class ApiLabel extends Splitter {

    private Project project;

    private ApiRepositoryPanel parent;

    public ApiLabel(TestApiBean testApiBean, ApiRepositoryPanel parent, Project project) {
        this.project = project;
        this.parent = parent;
        this.setFirstComponent(new LabelPanel(testApiBean,this));
        this.setSecondComponent(new ButtonPanel(testApiBean,this));
        this.setBorder(new LineBorder(Color.ORANGE));
    }

    class LabelPanel extends JPanel{
        private ApiLabel parent;

        LabelPanel(TestApiBean testApiBean,ApiLabel parent) {
            this.parent = parent;
            final FlowLayout mgr = new FlowLayout(FlowLayout.LEFT, 10, 1);
            this.setLayout(mgr);
            this.add(new JLabel("             "));
//            this.setLayout(new VerticalFlowLayout(0,0,0,true,true));
            final JLabel requestType = new JLabel(testApiBean.getRequestType().getDesc().toUpperCase());
            requestType.setForeground(JBColor.GREEN);
            this.add(requestType);
            this.add(new JLabel(testApiBean.getUri()));
            final JLabel requestName = new JLabel(testApiBean.getName());
            requestName.setForeground(JBColor.GRAY);
            this.add(requestName);
        }
    }

    class ButtonPanel extends JPanel{
        private ApiLabel parent;

        ButtonPanel(TestApiBean testApiBean,ApiLabel parent) {
            this.parent = parent;
            this.setLayout(new FlowLayout(FlowLayout.RIGHT,0,1));
//            this.setLayout(new VerticalFlowLayout(0,0,0,true,true));
            this.add(new MyClickButton("删除",(e)->{
                int update = Messages.showDialog(testApiBean.getUri(), "您确定要删除吗？", new String[]{"是", "否"}, 0, null);
                if (update==0){
                    ApiConfigService.getInstance(project).delete(testApiBean);
                    parent.parent.init();
                }
            },60));
            this.add(new MyClickButton("测试",
                    (e)-> TestApiUtil.getTestApiPanel(project).testApi(testApiBean),
                    60));
            this.add(new JLabel("             "));
        }
    }
}
