package com.dugq.component;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ImageLoader;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dugq on 2021/4/7.
 */
public interface IconFactory {
//   ImageIcon refresh = new ImageIcon("/img/icon/b10.png");
    Image img = ImageLoader.loadFromResource("/img/icon/b10.png",IconFactory.class);
   Icon refresh = IconLoader.getIcon("/img/icon/b10.png", IconFactory.class);

   static ImageIcon getRefresh(int width,int height){
      ImageIcon imageIcon = new ImageIcon(img);
      return imageIcon;
   }
}
