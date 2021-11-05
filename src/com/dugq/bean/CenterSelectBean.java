package com.dugq.bean;

import java.util.List;

/**
 * @author dugq
 * @date 2021/8/12 2:47 下午
 */
public class CenterSelectBean<T> {
    private T id;
    private String name;
    private List<CenterSelectBean<T>> children;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CenterSelectBean<T>> getChildren() {
        return children;
    }

    public void setChildren(List<CenterSelectBean<T>> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return name;
    }
}
