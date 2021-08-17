package com.dugq.pojo.ams;

/**
 * Created by dugq on 2019/12/26.
 */
public class SimpleApiVo {
    private Integer apiID;
    /**
     * api 接口名称
     */
    private String apiName;
    /**
     * 接口URi
     */
    private String apiURI;
    /**
     * 分组ID
     */
    private Integer groupID;
    /**
     * 父级分组ID
     */
    private Integer parentGroupID;
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 接口状态
     */
    private Integer apiStatus;
    /**
     * 请求类型
     */
    private Integer apiRequestType;
    private String apiUpdateTime;
    private Integer starred;
    private String partnerNickName;
    /**
     * 最近修改人名称
     */
    private String userNickName;
    /**
     * 修改人账号
     */
    private String userName;

    public Integer getApiID() {
        return apiID;
    }

    public void setApiID(Integer apiID) {
        this.apiID = apiID;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiURI() {
        return apiURI;
    }

    public void setApiURI(String apiURI) {
        this.apiURI = apiURI;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Integer getParentGroupID() {
        return parentGroupID;
    }

    public void setParentGroupID(Integer parentGroupID) {
        this.parentGroupID = parentGroupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public Integer getApiRequestType() {
        return apiRequestType;
    }

    public void setApiRequestType(Integer apiRequestType) {
        this.apiRequestType = apiRequestType;
    }

    public String getApiUpdateTime() {
        return apiUpdateTime;
    }

    public void setApiUpdateTime(String apiUpdateTime) {
        this.apiUpdateTime = apiUpdateTime;
    }

    public Integer getStarred() {
        return starred;
    }

    public void setStarred(Integer starred) {
        this.starred = starred;
    }

    public String getPartnerNickName() {
        return partnerNickName;
    }

    public void setPartnerNickName(String partnerNickName) {
        this.partnerNickName = partnerNickName;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
