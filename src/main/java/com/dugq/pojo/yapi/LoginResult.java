package com.dugq.pojo.yapi;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author dugq
 * @date 2021/8/12 6:17 下午
 */
public class LoginResult {
    private String username;
    private String role;
    @JSONField(name = "uid")
    private Long userId;
    private String email;
    @JSONField(name = "add_time")
    private Long addTime;
    @JSONField(name = "up_time")
    private Long upTime;
    private String type;
    private boolean study;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Long getUpTime() {
        return upTime;
    }

    public void setUpTime(Long upTime) {
        this.upTime = upTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isStudy() {
        return study;
    }

    public void setStudy(boolean study) {
        this.study = study;
    }
}
