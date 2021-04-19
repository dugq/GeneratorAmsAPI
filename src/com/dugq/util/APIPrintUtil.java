package com.dugq.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dugq.component.AmsToolPanel;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.EditorParam;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2021/3/22.
 */
public class APIPrintUtil extends BasePrintUtil{

    public static AmsToolPanel getAmsToolPanel(Project project){
        Content ams = getContentManager(project).findContent(AmsToolPanel.PanelId);
        return (AmsToolPanel)ams.getComponent();
    }

    public static void show(Project project){
        getKjjToolWindow(project).show(()->
                getContentManager(project).setSelectedContent(
                        getContentManager(project).findContent(AmsToolPanel.PanelId)));
    }

    public static void print(EditorParam param, Project project) {
        AmsToolPanel amsToolPanel = getAmsToolPanel(project);
        amsToolPanel.appendLine("接口描述: "+param.getApiName());
        amsToolPanel.appendLine((Objects.equals(param.getApiRequestType(),0)?"Post ":"Get ")+param.getApiURI());
        JSONObject request = Param2PrintJSON.param2Json(param.getApiRequestParam());
        JSONObject response = Param2PrintJSON.param2Json(param.getApiResultParam());
        amsToolPanel.appendLine("-------param--------");
        printJson(request,amsToolPanel);
        amsToolPanel.appendLine("-------result--------");
        printJson(response,amsToolPanel);
        show(project);
//        a(param);
    }

    private static String getName(String fullName){
        if (StringUtils.isBlank(fullName)) return fullName;
        if (fullName.contains(">")) return fullName.substring(fullName.lastIndexOf(">")+1);
        return fullName;
    }

    private static final List<Character> skipList = new ArrayList<>();
    static {
        skipList.add('\"');
        skipList.add('\'');
        skipList.add('[');
        skipList.add(']');
    }
    private static void printJson(JSONObject jsonObject, AmsToolPanel amsToolPanel){
        int blankNum=-1;
        String jsonString = JSON.toJSONString(jsonObject);
        //标记 key：每一行json的key默认为空字符串，value：每行json的value默认为空字符串， type：标记当前解析字符是属于key还是value。默认是key，在遇到"："字符后变更为value，当遇到"；"后复位为key
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        String type = null;
        LinkedList<String> lastLevel= new LinkedList<>();
        for (int index = 0; index<jsonString.length(); index++){
            char c = jsonString.charAt(index);
            if(skipList.contains(c)){
                continue;
            }else if (c=='{'){
//                printBlankTable(blankNum);
//                amsToolPanel.appendLine(c);
                blankNum++;
            }else if(c==':'){
                char nextChar = jsonString.charAt(index+1);
                if (nextChar=='{' || nextChar=='['){ //value为JSON时，打印本行，开始下一行。
                    printBlankTable(blankNum,amsToolPanel);
                    String thisKey = key.toString();
                    amsToolPanel.append(getKey(thisKey,lastLevel));
                    amsToolPanel.appendLine(" : "+getSubJsonType(nextChar));
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
                printKeyValueLine(blankNum, key, value, c,lastLevel,amsToolPanel);
                //把标记复位
                type="key";
                key = new StringBuilder();
                value = new StringBuilder();
            }else if(c=='}'){
                printKeyValueLine(blankNum, key, value, ' ',lastLevel, amsToolPanel);
                if (CollectionUtils.isNotEmpty(lastLevel)){
                    lastLevel.removeLast();
                }
                blankNum--;
//                printBlankTable(blankNum);
//                amsToolPanel.appendLine("");
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

    @NotNull
    private static String getSubJsonType(char c) {
        switch (c){
            case '{':return "Object";
            case '[': return "Array";
            default:return "UNKNOWN";
        }
    }

    private static String getKey(String thisKey,List<String> lastKeyList){
        if (CollectionUtils.isEmpty(lastKeyList)){
            return thisKey;
        }
        return StringUtils.join(lastKeyList,">")+">"+thisKey;
    }

    private static void printKeyValueLine(int blankNum, StringBuilder key, StringBuilder value, char c, List<String> lastLevel, AmsToolPanel amsToolPanel) {
        printBlankTable(blankNum, amsToolPanel);
        amsToolPanel.append(getKey(key.toString(),lastLevel));
        amsToolPanel.append(" : ");
        //打印value时，用$&分割类型和描述，把描述打印在','之后
        String msg = value.toString();
        if (msg.contains("$&")){
            String[] split = msg.split("\\$&");
            amsToolPanel.append(split[0]);
            amsToolPanel.append("; ");
            amsToolPanel.appendLine(split[1]);
        }else{
            amsToolPanel.append(msg);
            amsToolPanel.appendLine("");
        }
    }

    private static void printBlankTable(int num, AmsToolPanel amsToolPanel){
        for (int i =0 ; i<num; i++){
            amsToolPanel.append("  ");
        }
    }
}
