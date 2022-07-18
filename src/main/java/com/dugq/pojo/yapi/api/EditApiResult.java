package com.dugq.pojo.yapi.api;

/**
 * 保存API的结果。更新操作
 * @author dugq
 * @date 2021/8/13 2:42 下午
 */
public class EditApiResult {
    private Integer n;
    private Integer nModified;
    private Integer ok;

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getnModified() {
        return nModified;
    }

    public void setnModified(Integer nModified) {
        this.nModified = nModified;
    }

    public Integer getOk() {
        return ok;
    }

    public void setOk(Integer ok) {
        this.ok = ok;
    }
}
