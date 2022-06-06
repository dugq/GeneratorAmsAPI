package com.dugq.pojo.yapi.api;

/**
 * Request Param
 * @author dugq
 * @date 2021/8/11 11:45 下午
 */
public class ApiQueryBean {
    //参数名
    private String name;
    //是否必填 1:必填 0
    private int required;
    //描述
    private String desc;
    //事例
    private String example;

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
