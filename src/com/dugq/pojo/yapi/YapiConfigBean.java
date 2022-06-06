package com.dugq.pojo.yapi;

import java.io.Serializable;

/**
 * @author dugq
 * @date 2021/8/11 6:51 下午
 */
public class YapiConfigBean implements Serializable {
    private static final long serialVersionUID = -8013090380100636128L;

    public static final String LOGIN_TYPE_LDAP = "LDAP";
    public static final String LOGIN_TYPE_SOURCE = "source";

    private String email;

    private String password;

    private Long currentGroup;

    private Long currentProject;

    private Long userId;

    private String server;

    private String loginType;

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Long getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Long currentGroup) {
        this.currentGroup = currentGroup;
    }

    public Long getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Long currentProject) {
        this.currentProject = currentProject;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
