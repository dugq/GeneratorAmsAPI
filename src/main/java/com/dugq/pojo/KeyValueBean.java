package com.dugq.pojo;

import com.dugq.pojo.enums.ParamTypeEnum;

/**
 * @author dugq
 * @date 2021/7/8 5:43 下午
 */
public class KeyValueBean {

    private String key;

    private String value;

    private String desc;

    private Integer valueType = ParamTypeEnum.STRING.getType();

    public KeyValueBean() {
    }

    public KeyValueBean(String key, String value, String desc) {
        this.key = key;
        this.value = value;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }
}
