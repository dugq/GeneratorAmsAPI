package com.dugq.pojo.yapi.api;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author dugq
 * @date 2021/8/13 12:01 上午
 */
public class AddApiParam implements Serializable {
    private static final long serialVersionUID = 2742617191544666708L;

    @JSONField(name = "catid")
    private Long menuId;

    private String method;

    private String path;

    @JSONField(name = "project_id")
    private Long projectId;

    private String title;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
