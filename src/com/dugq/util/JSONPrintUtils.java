package com.dugq.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dugq.exception.ErrorException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2021/4/7.
 */
public class JSONPrintUtils {
    private static final List<Character> skipList = new ArrayList<>();

    static {
    }

    public static void printJson(String jsonObject, JTextArea textArea){
        if (StringUtils.isBlank(jsonObject)){
            appendLine(textArea,"NO RESPONSE");
        }else{
            printJson(JSON.parseObject(jsonObject),textArea);
        }
    }

    public static void printJson(JSONObject jsonObject, JTextArea textArea){
        int blankNum=0;
        String jsonString = JSON.toJSONString(jsonObject);
        //标记 key：每一行json的key默认为空字符串，value：每行json的value默认为空字符串， type：标记当前解析字符是属于key还是value。默认是key，在遇到"："字符后变更为value，当遇到"；"后复位为key
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        String type = null;
        LinkedList<String> lastLevel= new LinkedList<>();
        for (int index = 0; index<jsonString.length(); index++){
            char c = jsonString.charAt(index);
           if (c=='{'){
                printBlankTable(blankNum,textArea);
                appendLine(textArea,c);
                blankNum++;
            }else if(c==':'){
                char nextChar = jsonString.charAt(index+1);
                if (nextChar=='{' || nextChar=='['){ //value为JSON时，打印本行，开始下一行。
                    printBlankTable(blankNum,textArea);
                    String thisKey = key.toString();
                    textArea.append(getKey(thisKey,lastLevel));
                    if (nextChar=='['){
                        appendLine(textArea," : [");
                        blankNum++;
                        index++;
                    }else{
                        appendLine(textArea," : ");
                    }
                    //记录上级：
                    lastLevel.addLast(thisKey);
                    //把标记复位
                    type="key";
                    key = new StringBuilder();
                    value = new StringBuilder();
                }else{
                    type = "value";
                }
            }else if(c==','){
                printKeyValueLine(blankNum, key, value, c,lastLevel,textArea,false);
                //把标记复位
                type="key";
                key = new StringBuilder();
                value = new StringBuilder();
            }else if(c=='}'){
                printKeyValueLine(blankNum, key, value, ' ',lastLevel, textArea,true);
                if (CollectionUtils.isNotEmpty(lastLevel)){
                    lastLevel.removeLast();
                }
                blankNum--;
                printBlankTable(blankNum,textArea);
                if (index+1 <jsonString.length() && jsonString.charAt(index+1)==','){
                    appendLine(textArea,"},");
                    index++;
                }else if(index+1 <jsonString.length() && jsonString.charAt(index+1)==']'){
                    appendLine(textArea,"}");
                    index++;
                    blankNum--;
                    printBlankTable(blankNum,textArea);
                    if (index+1 <jsonString.length() && jsonString.charAt(index+1)==','){
                        appendLine(textArea,"],");
                        index++;
                    }else{
                        appendLine(textArea,"]");
                    }

                }else{
                    appendLine(textArea,"}");
                }

                type="key";
                key = new StringBuilder();
                value = new StringBuilder();
            }else if(c==']'){
               blankNum--;
               printBlankTable(blankNum,textArea);
               textArea.append("]");
               if (index+1 <jsonString.length() && jsonString.charAt(index+1)==','){
                   textArea.append(",");
                   index++;
               }
               appendLine(textArea,"");
           }else{
                if (type==null){
                    type = "key";
                    key = new StringBuilder();
                }
                if (Objects.equals(type, "key")){
                    key.append(c);
                }else if (Objects.equals(type, "value")){
                    value.append(c);
                }else{
                    throw new ErrorException("json解析有bug！");
                }
            }
        }
    }

    private static String getKey(String thisKey, List<String> lastKeyList){
//        if (CollectionUtils.isEmpty(lastKeyList)){
            return thisKey;
//        }
//        return StringUtils.join(lastKeyList,">")+">"+thisKey;
    }

    private static void printKeyValueLine(int blankNum, StringBuilder key, StringBuilder value, char c, List<String> lastLevel, JTextArea textArea,boolean last) {
        if (Objects.isNull(key) || key.length()<1){
            return;
        }
        printBlankTable(blankNum, textArea);
        textArea.append(getKey(key.toString(),lastLevel));
        textArea.append(" : ");
        //打印value时，用$&分割类型和描述，把描述打印在','之后
        String msg = value.toString();
        if (msg.contains("$&")){
            String[] split = msg.split("\\$&");
            textArea.append(split[0]);
            textArea.append("; ");
            textArea.append(split[1]);
        }else{
            textArea.append(msg);
        }
        if (!last){
            textArea.append(",");
        }
        appendLine(textArea,"");
    }

    private static void printBlankTable(int num, JTextArea textArea){
        for (int i =0 ; i<num; i++){
            textArea.append("  ");
        }
    }

    private static void appendLine(JTextArea textArea,String msg){
        if (StringUtils.isNotBlank(msg)){
            textArea.append(msg);
        }
        textArea.append("\n");
    }

    private static void appendLine(JTextArea textArea,Character msg){
        textArea.append(msg.toString());
        textArea.append("\n");
    }
}
