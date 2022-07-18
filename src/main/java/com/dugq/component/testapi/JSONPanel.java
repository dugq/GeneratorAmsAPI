package com.dugq.component.testapi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dugq.component.common.MyClickButton;
import com.dugq.util.TestApiUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.Map;

/**
 * @author dugq
 * @date 2021/7/12 4:58 下午
 */
public class JSONPanel extends JPanel {
    private Project project;
    private final JTextPane content;
    private final StyledDocument styledDocument;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JSONPanel(Project project) {
        this.project = project;
        this.setLayout(new VerticalFlowLayout(0,0,0,true,true));
        this.add(new MyClickButton("format JSON",(e)->{
            try{
                formatJSON();
            }catch (Exception ex){
                TestApiUtil.printException(ex, this.project);
            }
        },30));
        this.content = new JTextPane();
        this.content.setEditable(true);
        this.styledDocument = content.getStyledDocument();
        this.add(this.content);
    }

    private void formatJSON() {
        final String contentText = this.content.getText();
        clear();
        try{
            doFormatJSON(contentText);
        }catch (Exception e){
            e.printStackTrace();
            clear();
            append(contentText);
        }
    }

    private void doFormatJSON(String jsonString) {
        JsonElement parse = JsonParser.parseString(jsonString);
        if (parse.isJsonArray()){
            final JsonArray jsonElements = parse.getAsJsonArray();
            final String msg = gson.toJson(jsonElements);
            appendColorJson(msg);
        }else if (parse.isJsonNull()) {
        //ignore
        }else if (parse.isJsonPrimitive()) {
            appendLine(jsonString);
        }else{
            final JsonObject jsonObject = parse.getAsJsonObject();
            final String msg = gson.toJson(jsonObject);
            appendColorJson(msg);
        }
    }

    private void appendColorJson(String msg) {
        final String[] jsonLine = msg.split("\n");
        for (String s : jsonLine) {
            if (s.contains(":")){
                final String[] keyValue = s.split(":");
                append(keyValue[0],Color.YELLOW);
                append(" : ",Color.GRAY);
                String values = StringUtils.join(ArrayUtils.remove(keyValue, 0),":")+"\n";
                if (values.contains("{") || values.contains("[")){
                    append(values);
                }else{
                    append(values,Color.GREEN);
                }
            }else{
                appendLine(s);
            }
        }
    }

    public void clear() {
        try {
            styledDocument.remove(0,styledDocument.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public JSONPanel append(String msg){
        append(msg,Color.BLACK);
        return this;
    }

    public JSONPanel append(String msg,Color color){
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr,color);
        try {
            styledDocument.insertString(styledDocument.getLength(),msg,attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONPanel appendLine(String msg){
        append(msg+"\n");
        return this;
    }

    public String getContent(){
        return this.content.getText();
    }

    public void init(String paramJson) {
        clear();
        paramJson = dealRequestBody(paramJson);
        append(paramJson);
        this.formatJSON();
    }

    public String dealRequestBody(String requestBody) {
        final Map<String, String> globalParamMap = TestApiUtil.getTestApiPanel(project).getGlobalParamMap();
        if (StringUtils.isBlank(requestBody) || MapUtils.isEmpty(globalParamMap)){
            return requestBody;
        }
        if (StringUtils.isBlank(requestBody)){
            return "";
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(requestBody);
            putDefaultValue(jsonObject,globalParamMap);
            return jsonObject.toJSONString();
        }catch (Exception e){
            e.printStackTrace();
            return requestBody;
        }
    }

    private void putDefaultValue(JSONObject jsonObject, Map<String, String> globalParamMap) {
        for (String key : jsonObject.keySet()) {
            final Object jsonValue = jsonObject.get(key);
            if (jsonValue instanceof JSONArray){
                final JSONArray jsonArray = (JSONArray) jsonValue;
                for (Object object : jsonArray) {
                    if (object instanceof JSONObject){
                        putDefaultValue((JSONObject)object,globalParamMap);
                    }
                }
            }else if(jsonValue instanceof JSONObject){
                putDefaultValue((JSONObject)jsonValue,globalParamMap);
            }else{
                if (StringUtils.isBlank(jsonObject.getString(key)) && StringUtils.isNotBlank(globalParamMap.get(key))){
                    jsonObject.put(key,globalParamMap.get(key));
                }
            }
        }
    }
}
