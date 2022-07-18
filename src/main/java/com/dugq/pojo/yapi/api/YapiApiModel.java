package com.dugq.pojo.yapi.api;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.http.Header;

import java.util.List;

/**
 * YAPI API完整模型
 * @author dugq
 * @date 2021/8/11 11:36 下午
 */
public class YapiApiModel {

    private String title;

    @JSONField(name = "api_opened")
    private boolean apiOpened;

    @JSONField(name = "catid")
    private long menuId;

    private String desc;

    private long id;

    private String markdown;

    private String method;

    private String path;

    @JSONField(name = "req_body_is_json_schema")
    private boolean json = true;

    @JSONField(name = "req_body_other")
    private String reqBody;

    @JSONField(name = "req_body_type")
    private String reqBodyType = "json";

    @JSONField(name = "req_headers")
    private List<Header> headers;

    @JSONField(name = "req_query")
    private List<ApiQueryBean> reqQuery;

    @JSONField(name = "res_body")
    private String resBody;

    @JSONField(name = "res_body_is_json_schema")
    private boolean resBodyIsJson=true;

    @JSONField(name = "res_body_type")
    private String resBodyType= "json";

    private String status = "done";

    private boolean switch_notice = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isApiOpened() {
        return apiOpened;
    }

    public void setApiOpened(boolean apiOpened) {
        this.apiOpened = apiOpened;
    }

    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public String getReqBody() {
        return reqBody;
    }

    public void setReqBody(String reqBody) {
        this.reqBody = reqBody;
    }

    public String getReqBodyType() {
        return reqBodyType;
    }

    public void setReqBodyType(String reqBodyType) {
        this.reqBodyType = reqBodyType;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public List<ApiQueryBean> getReqQuery() {
        return reqQuery;
    }

    public void setReqQuery(List<ApiQueryBean> reqQuery) {
        this.reqQuery = reqQuery;
    }

    public String getResBody() {
        return resBody;
    }

    public void setResBody(String resBody) {
        this.resBody = resBody;
    }

    public boolean isResBodyIsJson() {
        return resBodyIsJson;
    }

    public void setResBodyIsJson(boolean resBodyIsJson) {
        this.resBodyIsJson = resBodyIsJson;
    }

    public String getResBodyType() {
        return resBodyType;
    }

    public void setResBodyType(String resBodyType) {
        this.resBodyType = resBodyType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSwitch_notice() {
        return switch_notice;
    }

    public void setSwitch_notice(boolean switch_notice) {
        this.switch_notice = switch_notice;
    }
}
