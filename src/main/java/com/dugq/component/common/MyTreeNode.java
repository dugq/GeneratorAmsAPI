package com.dugq.component.common;

import com.dugq.bean.TreeNodeBean;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author dugq
 * @date 2022/7/5 11:36 下午
 */
public class MyTreeNode<T extends TreeNodeBean> extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1800689821387208197L;
    private final T treeNodeBean;

    public MyTreeNode(T t) {
        super(t);
        this.treeNodeBean = t;
    }

    public T getTreeNodeBean() {
        return treeNodeBean;
    }


    public static MyTreeNode<TreeNodeBean> rootNode(){
        return new MyTreeNode<>(new TreeNodeBean("ROOT"));
    }
}
