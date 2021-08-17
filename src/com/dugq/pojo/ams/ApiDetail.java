package com.dugq.pojo.ams;

import java.util.List;

/**
 * @author dugq
 * @date 2021/8/16 11:37 上午
 */
public class ApiDetail {
    private BaseInfo baseInfo;

    private List<RequestParam> requestInfo;

    private List<RequestParam> resultInfo;

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public List<RequestParam> getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(List<RequestParam> requestInfo) {
        this.requestInfo = requestInfo;
    }

    public List<RequestParam> getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(List<RequestParam> resultInfo) {
        this.resultInfo = resultInfo;
    }

    public static class BaseInfo{
        private String afterInject;
        private String apiName;
        private String apiNote;
        private String apiNoteRaw;
        private String apiNoteType;
        private String apiProtocol;
        private String apiRequestParamType;
        private String apiRequestRaw;
        private String apiRequestType;
        private String apiStatus;
        private String apiURI;

        public String getAfterInject() {
            return afterInject;
        }

        public void setAfterInject(String afterInject) {
            this.afterInject = afterInject;
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

        public String getApiNoteType() {
            return apiNoteType;
        }

        public void setApiNoteType(String apiNoteType) {
            this.apiNoteType = apiNoteType;
        }

        public String getApiProtocol() {
            return apiProtocol;
        }

        public void setApiProtocol(String apiProtocol) {
            this.apiProtocol = apiProtocol;
        }

        public String getApiRequestParamType() {
            return apiRequestParamType;
        }

        public void setApiRequestParamType(String apiRequestParamType) {
            this.apiRequestParamType = apiRequestParamType;
        }

        public String getApiRequestRaw() {
            return apiRequestRaw;
        }

        public void setApiRequestRaw(String apiRequestRaw) {
            this.apiRequestRaw = apiRequestRaw;
        }

        public String getApiRequestType() {
            return apiRequestType;
        }

        public void setApiRequestType(String apiRequestType) {
            this.apiRequestType = apiRequestType;
        }

        public String getApiStatus() {
            return apiStatus;
        }

        public void setApiStatus(String apiStatus) {
            this.apiStatus = apiStatus;
        }

        public String getApiURI() {
            return apiURI;
        }

        public void setApiURI(String apiURI) {
            this.apiURI = apiURI;
        }
    }

}
