package com.dugq.pojo.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Created by dugq on 2019/12/26.
 */
public enum  RequestType {
    get(1,"get"),
    post(0,"post");
    private int type;
    private String desc;

    RequestType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static RequestType getByDesc(String desc){
        if(StringUtils.equals("get",desc)){
            return get;
        }
        if(StringUtils.equals("post",desc)){
            return post;
        }
        return null;
    }
}
