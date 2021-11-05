package com.dugq.pojo.ams;

/**
 * Created by dugq on 2019/12/26.
 */
public class AmsApiSearchParam {
    private Integer projectID = 118;
    private Integer groupID = -1;
    private Integer orderBy = 3;
    private Integer asc = 0;
    private String tips;

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getAsc() {
        return asc;
    }

    public void setAsc(Integer asc) {
        this.asc = asc;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
