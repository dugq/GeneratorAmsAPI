package com.dugq.pojo.yapi;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author dugq
 * @date 2021/8/11 9:12 下午
 */
public class YapiMenuBean {
    @JSONField(name = "_id")
    private long id;

    private String name;

    @JSONField(name = "project_id")
    private Long projectId;

    private String desc;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
