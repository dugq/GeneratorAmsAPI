package com.dugq.pojo.yapi;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author dugq
 * @date 2021/8/11 8:39 下午
 */
public class YapiProjectBean implements Serializable {

    private static final long serialVersionUID = 8315797031657355922L;
    private String name;

    @JSONField(name="_id")
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
