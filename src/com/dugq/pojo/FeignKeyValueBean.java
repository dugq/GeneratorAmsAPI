package com.dugq.pojo;

/**
 * @author dugq
 * @date 2021/7/18 5:45 下午
 */
public class FeignKeyValueBean {
    private Integer index;
    private String key;
    private String value;
    private String valueType;

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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
}
