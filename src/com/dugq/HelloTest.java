package com.dugq;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by dugq on 2019/12/15.
 */
public class HelloTest extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}
