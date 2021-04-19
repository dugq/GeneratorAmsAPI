package com.dugq.component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by dugq on 2021/4/7.
 */
public   interface MyClickListener extends MouseListener {
    @Override
     default void mouseClicked(MouseEvent e) {

    }

    @Override
    default  void mousePressed(MouseEvent e) {

    }

    @Override
    default  void mouseReleased(MouseEvent e) {

    }

    @Override
    default  void mouseEntered(MouseEvent e) {

    }

    @Override
    default  void mouseExited(MouseEvent e) {

    }
}
