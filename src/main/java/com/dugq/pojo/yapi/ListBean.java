package com.dugq.pojo.yapi;

import java.io.Serializable;
import java.util.List;

/**
 * @author dugq
 * @date 2021/8/12 6:51 下午
 */
public class ListBean<T> implements Serializable {

    private static final long serialVersionUID = 9035822385668754984L;
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
