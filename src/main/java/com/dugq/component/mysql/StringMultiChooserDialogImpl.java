package com.dugq.component.mysql;

import com.dugq.bean.TreeNodeBean;
import com.dugq.component.common.MultiChooserDialogImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2022/7/6 1:09 上午
 */
public class StringMultiChooserDialogImpl extends MultiChooserDialogImpl<TreeNodeBean>{

    public StringMultiChooserDialogImpl(@Nullable Project project, List<String> allColumns) {
        super(project, allColumns.stream().map(TreeNodeBean::new).collect(Collectors.toList()));
    }

    public List<String> getSelectedList() {
        final List<TreeNodeBean> selectList = super.getSelectList();
        return selectList.stream().map(TreeNodeBean::getName).collect(Collectors.toList());
    }
}
