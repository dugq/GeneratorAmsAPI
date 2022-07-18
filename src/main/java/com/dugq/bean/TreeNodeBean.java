package com.dugq.bean;

/**
 * @author dugq
 * @date 2022/7/5 11:36 下午
 */
public class TreeNodeBean {
    private String name;

    public TreeNodeBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
