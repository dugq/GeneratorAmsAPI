package com.dugq.component.common;

import com.dugq.bean.TreeNodeBean;
import com.intellij.ide.hierarchy.HierarchyNodeRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2022/7/5 11:19 下午
 */
public class MultiChooserDialogImpl<T extends TreeNodeBean> extends DialogWrapper {
    private Project myProject;
    private Tree myTree;
    private Set<TreePath> treePathList = new HashSet<>();
    private List<T> allNodeBeans;

    public MultiChooserDialogImpl(@Nullable Project project, List<T> allNodeBeans) {
        super(project);
        this.myProject = project;
        this.allNodeBeans = allNodeBeans;
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel mainPanel = new JBPanel<>();
        final MyTreeNode<TreeNodeBean> rootNode = MyTreeNode.rootNode();
        final DefaultTreeModel model = new DefaultTreeModel(rootNode);
        Integer index = 0;
        for (T nodeBean : allNodeBeans) {
            model.insertNodeInto( new MyTreeNode<>(nodeBean),rootNode,index++);
        }
        myTree = new Tree(model);
        myTree.setRootVisible(false);
        myTree.setShowsRootHandles(true);
        myTree.expandRow(0);
        myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        myTree.setCellRenderer(new HierarchyNodeRenderer());
        myTree.setPreferredSize(JBUI.size(300, 300));


        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTree);
        scrollPane.setPreferredSize(JBUI.size(500, 300));
        scrollPane.putClientProperty(UIUtil.KEEP_BORDER_SIDES, SideBorder.RIGHT | SideBorder.LEFT | SideBorder.BOTTOM);
        myTree.addMouseListener(new MyClickListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                final TreePath[] selectionPaths = myTree.getSelectionPaths();
//                for (TreePath selectionPath : selectionPaths) {
//                    if (treePathList.contains(selectionPath)){
//                        treePathList.remove(selectionPath);
//                    }else{
//                        treePathList.add(selectionPath);
//                    }
//                }
//                myTree.addSelectionPaths(treePathList.toArray(new TreePath[]{}));
            }
        });
        mainPanel.add(myTree);
        return mainPanel;
    }

    @Override
    protected @Nullable JComponent createNorthPanel() {
        JPanel panel = new JBPanel<>();
        panel.add(new JLabel("mac按住command可多选,windows按住ctrl"));
        return panel;
    }

    public List<T> getSelectList() {
        return Arrays.stream(myTree.getSelectionPaths()).map(path->((MyTreeNode<T>)path.getLastPathComponent()).getTreeNodeBean()).collect(Collectors.toList());
    }
}
