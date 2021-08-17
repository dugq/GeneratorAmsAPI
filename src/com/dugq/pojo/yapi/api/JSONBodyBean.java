package com.dugq.pojo.yapi.api;

import com.alibaba.fastjson.annotation.JSONField;
import com.dugq.pojo.ams.ParamSelectValue;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RequestBody 和 responseBody的JSON格式化对象
 * @author dugq
 * @date 2021/8/11 11:50 下午
 */
public class JSONBodyBean {
    /**
     * 类型 {@link YapiParamType}
     */
    private String type;
    /**
     * 字段描述 root 固定为：<code>empty object</code>
     */
    private String title;
    /**
     * 当type = object 时此属性有效。容纳子属性
     */
    private Map<String, JSONBodyBean> properties;
    /**
     * 必填子属性列表
     */
    private List<String> required;
    /**
     * 描述
     */
    private String description;
    /**
     * 枚举值列表
     */
    @JSONField(name = "enum")
    private List<String> enums;
    /**
     * 枚举值描述。一个字符串里放全部枚举值的描述
     */
    private String enumDesc;

    /**
     * 当type = array 时此属性有效
     * 当数组为基本类型数组时，items 的title 固定为items，type=基本类型
     * 当数组为对象类型数组时，items的title 固定为items，type=object, items的properties容纳对象的具体属性
     */
    private JSONBodyBean items;


    //最大值最小值开关 以及值域
    private boolean exclusiveMinimum;
    private Integer minimum;
    private boolean exclusiveMaximum;
    private Integer maximum;

    private Mock mock;

    @JSONField(name = "default")
    private String defaultValue;


    public JSONBodyBean() {
        properties = new HashMap<>();
        required = new ArrayList<>();
    }

    public static JSONBodyBean of(String tile, YapiParamType paramType, String desc) {
        JSONBodyBean reqBodyBean = new JSONBodyBean();
        reqBodyBean.setTitle(tile);
        reqBodyBean.setType(paramType.getName());
        reqBodyBean.setDescription(desc);
        return reqBodyBean;
    }

    public void addEnums(List<ParamSelectValue> enums){
        if (CollectionUtils.isEmpty(enums)){
            return;
        }
        this.enums = enums.stream().map(ParamSelectValue::getValue).collect(Collectors.toList());
        this.enumDesc = enums.stream().map(ParamSelectValue::getValueDescription).collect(Collectors.joining(";"));
    }

    public void addProps(JSONBodyBean reqBodyBean, boolean isRequired){
        properties.put(reqBodyBean.getTitle(),reqBodyBean);
        if (isRequired){
            required.add(reqBodyBean.getTitle());
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, JSONBodyBean> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, JSONBodyBean> properties) {
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEnums() {
        return enums;
    }

    public void setEnums(List<String> enums) {
        this.enums = enums;
    }

    public JSONBodyBean getItems() {
        return items;
    }

    public void setItems(JSONBodyBean items) {
        this.items = items;
    }

    public String getEnumDesc() {
        return enumDesc;
    }

    public void setEnumDesc(String enumDesc) {
        this.enumDesc = enumDesc;
    }

    public boolean isExclusiveMinimum() {
        return exclusiveMinimum;
    }

    public void setExclusiveMinimum(boolean exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public boolean isExclusiveMaximum() {
        return exclusiveMaximum;
    }

    public void setExclusiveMaximum(boolean exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public Mock getMock() {
        return mock;
    }

    public void setMock(Mock mock) {
        this.mock = mock;
    }

    public static class Mock{
        private String mock;

        public String getMock() {
            return mock;
        }

        public void setMock(String mock) {
            this.mock = mock;
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
