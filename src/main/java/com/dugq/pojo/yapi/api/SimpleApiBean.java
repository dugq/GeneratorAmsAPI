package com.dugq.pojo.yapi.api;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * API的列表接口返回的简单API对象
 * @author dugq
 * @date 2021/8/11 9:18 下午
 */
public class SimpleApiBean {

    private String title;
    @JSONField(name = "catid")
    private long catId;

    private String path;

    @JSONField(name = "_id")
    private long id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
