package com.dugq.pojo.yapi;

import java.io.Serializable;

/**
 * @author dugq
 * @date 2021/8/11 5:35 下午
 */
public class LoginParam implements Serializable {
    private static final long serialVersionUID = 1351661517526570886L;

    private String email;
    private String password;
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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
}
