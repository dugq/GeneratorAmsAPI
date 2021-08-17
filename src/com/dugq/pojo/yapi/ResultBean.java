package com.dugq.pojo.yapi;

/**
 * @author dugq
 * @date 2021/8/11 8:32 下午
 */
public class ResultBean<T> {
    private int errcode;
    private String errmsg;
    private T data;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess(){
        return errcode==0;
    }
}
