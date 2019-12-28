package com.dugq.pojo;

import java.util.List;

/**
 * Created by dugq on 2019/12/25.
 */
public class RequestParam {
    /**
     * 是否非空
     * 0：true
     * 1：false
     */
    private Integer paramNotNull = 1;
    /**
     * 参数说明
     */
    private String paramName;
    /**
     * 参数名
     */
    private String paramKey;

    /**
     * 事例
     */
    private String paramValue;

    private Integer paramType;

    private String paramLimit;
    /**
     * 详情
     */
    private String paramNote;

    /**
     * 可能值
     */
    private List<ParamSelectValue> paramValueList;

    /**
     * 顺序
     */
    private Integer $index;

    public Integer getParamNotNull() {
        return paramNotNull;
    }

    public void setParamNotNull(Integer paramNotNull) {
        this.paramNotNull = paramNotNull;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Integer getParamType() {
        return paramType;
    }

    public void setParamType(Integer paramType) {
        this.paramType = paramType;
    }

    public String getParamLimit() {
        return paramLimit;
    }

    public void setParamLimit(String paramLimit) {
        this.paramLimit = paramLimit;
    }

    public String getParamNote() {
        return paramNote;
    }

    public void setParamNote(String paramNote) {
        this.paramNote = paramNote;
    }

    public List<ParamSelectValue> getParamValueList() {
        return paramValueList;
    }

    public void setParamValueList(List<ParamSelectValue> paramValueList) {
        this.paramValueList = paramValueList;
    }

    public Integer get$index() {
        return $index;
    }

    public void set$index(Integer $index) {
        this.$index = $index;
    }

}


