package com.dugq.component.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * @author dugq
 * @date 2021/7/8 5:16 下午
 */
public class MyClickButton extends JButton {

    public MyClickButton(String text, Consumer<MouseEvent> predicate){
        this(text,predicate,30);
    }

    public MyClickButton(String text, Consumer<MouseEvent> predicate,int width){
        this.setText(text);
        this.setPreferredSize(new Dimension(width,30));
        this.addMouseListener(new MyClickListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                predicate.accept(e);
            }
        });
    }

}
