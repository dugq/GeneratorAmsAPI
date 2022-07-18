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
        if (StringUtils.isBlank(desc)){
            return null;
        }
        desc = desc.toLowerCase();
        if(StringUtils.equals("get",desc)){
            return get;
        }
        if(StringUtils.equals("post",desc)){
            return post;
        }
        return null;
    }

    public static String getDescByType(Integer type){
        if (type==1){
            return get.desc;
        }
        if (type==0){
            return post.getDesc();
        }
        return "UNKNOWN";
    }
}
