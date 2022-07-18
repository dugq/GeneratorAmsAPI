package com.dugq.component.tool;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by dugq on 2021/4/7.
 */
public class KjjMenu implements MouseListener {
    private final JComponent invokerComponent;
    private final ActionPopupMenu actionPopupMenu;

    public KjjMenu(JComponent invokerComponent) {
        this.invokerComponent = invokerComponent;
        final ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup kjjMenuGroup = (DefaultActionGroup) actionManager.getAction("KJJ menu group");
        this.actionPopupMenu = actionManager.createActionPopupMenu("KJJ menu",
                kjjMenuGroup);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton()==3){
            actionPopupMenu.getComponent().show(invokerComponent,e.getX(),e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
