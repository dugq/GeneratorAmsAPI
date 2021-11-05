package com.dugq.pojo.ams;

import java.util.List;

/**
 * Created by dugq on 2019/12/26.
 */
public class GroupVo {
    private Integer groupID;
    private String groupName;
    private List<GroupVo> childGroupList;
    private Long parentGroupID;

    public Long getParentGroupID() {
        return parentGroupID;
    }

    public void setParentGroupID(Long parentGroupID) {
        this.parentGroupID = parentGroupID;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<GroupVo> getChildGroupList() {
        return childGroupList;
    }

    public void setChildGroupList(List<GroupVo> childGroupList) {
        this.childGroupList = childGroupList;
    }
}
