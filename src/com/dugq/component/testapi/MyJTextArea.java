package com.dugq.component.testapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author dugq
 * @date 2021/7/18 9:27 下午
 */
public class MyJTextArea extends JTextArea {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public MyJTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
        formatJson();
        this.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                formatJson();
            }
        });
    }

    private void formatJson() {
        final String fullText = getText();
        setText("");
        JsonElement parse = JsonParser.parseString(fullText);
        if (parse.isJsonArray()){
            final JsonArray jsonElements = parse.getAsJsonArray();
            final String msg = gson.toJson(jsonElements);
            append(msg);
        }else if (parse.isJsonNull()) {
            //ignore
        }else if (parse.isJsonPrimitive()) {
            append(fullText);
        }else{
            final JsonObject jsonObject = parse.getAsJsonObject();
            final String msg = gson.toJson(jsonObject);
            append(msg);
        }
    }
}
