package com.dugq.component.testapi;

import com.dugq.component.common.MyClickButton;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.FeignKeyValueBean;
import com.dugq.pojo.enums.ParamTypeEnum;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ScrollPaneFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Objects;

/**
 * Created by dugq on 2021/4/7.
 */
public class FeignParameterLinePanel extends JPanel{

    private final JTextField index;
    private final JTextField fieldName;
    private JComboBox<String> valueType = new ComboBox(ParamTypeEnum.getAllTypeNames());
    private JTextComponent value;
    private final JButton deleteButton;
    private final FeignParameterPanel parentPanel;

    //写代码是适配最小屏幕
    private static final int minLength = 1150;
    private final FeignKeyValueBean content;

    public FeignParameterLinePanel(FeignParameterPanel parentPanel, FeignKeyValueBean paramBean) {
        valueType.setSize(10,2);
        this.parentPanel = parentPanel;
        int width = parentPanel.getWidth();
        if (width<minLength){
            width = minLength;
        }
        this.index = new JTextField(width*5/1150);
        index.setBackground(Color.white);
        index.setForeground(Color.black);
        add(index);

        this.fieldName = new JTextField(width*15/1150);
        fieldName.setBackground(Color.white);
        fieldName.setForeground(Color.PINK);
        add(fieldName);

        add(valueType);

        if (Objects.nonNull(paramBean)){
            index.setText(Objects.isNull(paramBean.getIndex())?"":paramBean.getIndex().toString());
            fieldName.setText(paramBean.getKey());
            value = getTextComponent(paramBean.getValueType(),paramBean.getValue());
            valueType.setSelectedItem(paramBean.getValueType());
        }else{
            value = getTextComponent(null,"");
        }
        valueType.addItemListener(e -> {
            final String selectedItem = (String)valueType.getSelectedItem();
            remove(3);
            value = getTextComponent(selectedItem,value.getText());
            add(ScrollPaneFactory.createScrollPane(value),3);
            updateUI();
        });
        add(ScrollPaneFactory.createScrollPane(value));
        deleteButton = new MyClickButton("-",(e)->removeSelf());
        add(deleteButton);
        this.content = new FeignKeyValueBean();
    }

    private JTextComponent getTextComponent(String valueType,String value){
        int width = parentPanel.getWidth();
        if (width<1150){
            width = 1150;
        }
        if (needTextArea(valueType)){
            return new MyJTextArea(value, 5, width * 25 / 1150);
        }else{
            return new JTextField(value, width * 15 / 1150);
        }
    }

    private boolean needTextArea(String valueType) {
        return StringUtils.equals(valueType, ParamTypeEnum.ARRAY.getName()) || StringUtils.equals(valueType, ParamTypeEnum.JSON.getName());
    }

    public FeignKeyValueBean getContent(){
        content.setKey(this.fieldName.getText());
        final String indexText = this.index.getText();
        if (StringUtils.isNotBlank(indexText)){
            if (NumberUtils.isCreatable(indexText)){
                content.setIndex(Integer.valueOf(indexText));
            }else{
                throw new ErrorException("index不是合法数字");
            }
        }
        content.setValue(this.value.getText());
        final String selectedItem = (String) valueType.getSelectedItem();
        if (StringUtils.isNotBlank(selectedItem)){
            final ParamTypeEnum paramTypeEnum = ParamTypeEnum.getByName(selectedItem);
            if (Objects.nonNull(paramTypeEnum)){
                content.setValueType(paramTypeEnum.getName());
            }
        }
       return content;
    }

    public void removeSelf() {
        this.parentPanel.removeKeyValuePanel(this);
    }

}
