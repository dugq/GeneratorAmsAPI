package com.dugq.component;

import com.dugq.exception.ErrorException;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Objects;

/**
 * Created by dugq on 2021/3/23.
 */
public class WindowComponent {

    private static ToolWindow toolWindow;

    private static JTextArea textArea;

    private static boolean isEnter = true;

    private static ScrollPane scrollPane;

    private static  JPanel main;

    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public static void hide(){
        toolWindow.hide(null);
    }

    public static void show(){
        toolWindow.show(()->{
            Dimension textSize = new Dimension();
            textSize.setSize(screenSize.getWidth()-50,main.getHeight()-10);
            scrollPane.setSize(textSize);
        } );
    }

    public static void init(ToolWindow toolWindow) {
        main = new JPanel();
        WindowComponent.toolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        WindowComponent.textArea = new JTextArea();

        scrollPane = new ScrollPane();
        Dimension textSize = new Dimension();
        textSize.setSize(screenSize.getWidth()-50,screenSize.getHeight()/3);
        scrollPane.setSize(textSize);

        main.add(scrollPane);
        scrollPane.add(textArea);
        main.addComponentListener(ResizeListener());
        Content content = contentFactory.createContent(main,"", false);
        textArea.setBackground(Color.BLACK);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);
    }

    @NotNull
    public static ComponentListener ResizeListener() {
        return new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension textSize = new Dimension();
                textSize.setSize(screenSize.getWidth()-50,main.getHeight()-10);
                scrollPane.setSize(textSize);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        };
    }

    public static void printLine(String msg){
        if (Objects.isNull(textArea)){
            throw new ErrorException("toolWindow 未初始化");
        }
        if (!isEnter){
            textArea.append("\n");
        }
        textArea.append(msg);
        textArea.append("\n");
        isEnter = true;
    }

    public static void appendLine(String msg){
        if (Objects.isNull(textArea)){
            throw new ErrorException("toolWindow 未初始化");
        }
        textArea.append(msg);
        textArea.append("\n");
        isEnter = true;
    }

    public static void appendLine(char msg){
        appendLine(String.valueOf(msg));
    }

    public static void append(String msg){
        if (Objects.isNull(textArea)){
            throw new ErrorException("toolWindow 未初始化");
        }
        textArea.append(msg);
        isEnter = false;
    }

    public static void append(char msg){
        append(String.valueOf(msg));
    }


    public static void clear(){
        textArea.setText(" ");
        scrollPane.remove(textArea);
        textArea = new JTextArea();
        scrollPane.add(textArea);
        textArea.setBackground(Color.BLACK);

    }

}
