package com.dugq.postman.bean;

import java.io.Serializable;

/**
 * Created by dugq on 2020/4/10.
 */
public class PostBody implements Serializable {
    private String mode = "raw";
    private String raw;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
