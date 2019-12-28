package com.dugq.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dugq on 2019/12/25.
 */

public class EditorParam implements Serializable {
   private Integer projectID= 118;
   private Integer groupID = 861;
   private Integer apiID;
   private List<RequestParam> apiRequestParam;
   private List<RequestParam> apiResultParam;
   private Integer starred = 0;
   private Integer apiStatus = 0;
   private Integer apiProtocol = 0;
   private Integer apiRequestType = 0;
    /**
     * uri
     */
   private String apiURI;
    /**
     * 名称
     */
    private String apiName;
    private String apiSuccessMock;
    private String apiFailureMock;
    private List apiHeader;
    private String apiNote;
    private String apiNoteRaw;
    private Integer apiNoteType;
    private Integer apiRequestParamType;
    private String apiRequestRaw;
    private List mockRule;
    private Object mockResult= new Object();
    private Object mockConfig= new Object();

    public List getApiHeader() {
        return apiHeader;
    }

    public void setApiHeader(List apiHeader) {
        this.apiHeader = apiHeader;
    }

    public Object getMockConfig() {
        return mockConfig;
    }

    public void setMockConfig(Object mockConfig) {
        this.mockConfig = mockConfig;
    }

    public void setMockRule(List mockRule) {
        this.mockRule = mockRule;
    }

    public List getMockRule() {
        return mockRule;
    }

    public Object getMockResult() {
        return mockResult;
    }

    public void setMockResult(Object mockResult) {
        this.mockResult = mockResult;
    }

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

    public Integer getApiID() {
        return apiID;
    }

    public void setApiID(Integer apiID) {
        this.apiID = apiID;
    }

    public List<RequestParam> getApiRequestParam() {
        return apiRequestParam;
    }

    public void setApiRequestParam(List<RequestParam> apiRequestParam) {
        this.apiRequestParam = apiRequestParam;
    }

    public List<RequestParam> getApiResultParam() {
        return apiResultParam;
    }

    public void setApiResultParam(List<RequestParam> apiResultParam) {
        this.apiResultParam = apiResultParam;
    }

    public Integer getStarred() {
        return starred;
    }

    public void setStarred(Integer starred) {
        this.starred = starred;
    }

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public Integer getApiProtocol() {
        return apiProtocol;
    }

    public void setApiProtocol(Integer apiProtocol) {
        this.apiProtocol = apiProtocol;
    }

    public Integer getApiRequestType() {
        return apiRequestType;
    }

    public void setApiRequestType(Integer apiRequestType) {
        this.apiRequestType = apiRequestType;
    }

    public String getApiURI() {
        return apiURI;
    }

    public void setApiURI(String apiURI) {
        this.apiURI = apiURI;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }


    public String getApiNote() {
        return apiNote;
    }

    public void setApiNote(String apiNote) {
        this.apiNote = apiNote;
    }

    public String getApiNoteRaw() {
        return apiNoteRaw;
    }

    public void setApiNoteRaw(String apiNoteRaw) {
        this.apiNoteRaw = apiNoteRaw;
    }

    public String getApiSuccessMock() {
        return apiSuccessMock;
    }

    public void setApiSuccessMock(String apiSuccessMock) {
        this.apiSuccessMock = apiSuccessMock;
    }

    public String getApiFailureMock() {
        return apiFailureMock;
    }

    public void setApiFailureMock(String apiFailureMock) {
        this.apiFailureMock = apiFailureMock;
    }

    public Integer getApiNoteType() {
        return apiNoteType;
    }

    public void setApiNoteType(Integer apiNoteType) {
        this.apiNoteType = apiNoteType;
    }

    public Integer getApiRequestParamType() {
        return apiRequestParamType;
    }

    public void setApiRequestParamType(Integer apiRequestParamType) {
        this.apiRequestParamType = apiRequestParamType;
    }

    public String getApiRequestRaw() {
        return apiRequestRaw;
    }

    public void setApiRequestRaw(String apiRequestRaw) {
        this.apiRequestRaw = apiRequestRaw;
    }

}
