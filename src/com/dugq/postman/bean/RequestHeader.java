package com.dugq.postman.bean;

import java.io.Serializable;

/**
 * Created by dugq on 2020/4/10.
 */
public class RequestHeader implements Serializable {
    private String key;
    private String value;
    private String type;
    private boolean disabled  =false;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
