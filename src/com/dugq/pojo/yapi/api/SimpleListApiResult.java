package com.dugq.pojo.yapi.api;

import java.util.List;

/**
 * 查询API列表返回API列表对象
 * @author dugq
 * @date 2021/8/11 9:48 下午
 */
public class SimpleListApiResult {
    //API列表
    private List<SimpleApiBean> list;
    //总条数
    private int count;
    //总页数
    private int total;

    public List<SimpleApiBean> getList() {
        return list;
    }

    public void setList(List<SimpleApiBean> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
