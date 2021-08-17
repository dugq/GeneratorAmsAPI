package com.dugq.pojo.yapi;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author dugq
 * @date 2021/8/11 7:44 下午
 * 空间对象
 */
public class GroupBean {
    @JSONField(name = "group_name")
    private String groupName;

    @JSONField(name = "_id")
    private String id;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
