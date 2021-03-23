package com.dugq.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dugq.component.WindowComponent;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.EditorParam;
import com.dugq.pojo.RequestParam;
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
public class PrintUtil {

    public static void print(EditorParam param) {
        WindowComponent.show();
        WindowComponent.clear();
        WindowComponent.printLine("接口描述: "+param.getApiName());
        WindowComponent.printLine(Objects.equals(param.getApiRequestType(),1)?"Post ":"Get "+param.getApiURI());
        JSONObject request = Param2PrintJSON.param2Json(param.getApiRequestParam());
        JSONObject response = Param2PrintJSON.param2Json(param.getApiResultParam());
        WindowComponent.printLine("-------param--------");
        printJson(request);
        WindowComponent.printLine("-------result--------");
        printJson(response);
//        a(param);
    }

    private static void a(EditorParam param) {
        List<RequestParam> paramList = param.getApiRequestParam();
        if (CollectionUtils.isNotEmpty(paramList)){
            WindowComponent.printLine("");
            WindowComponent.append("请求参数名");
            WindowComponent.append("\t"+"请求参数类型");
            WindowComponent.appendLine("\t"+"请求参数说明");
            for (RequestParam requestParam : paramList) {
                WindowComponent.append(getName(requestParam.getParamKey()));
                WindowComponent.append("\t"+ ApiParamBuildUtil.getType(requestParam.getParamType()));
                WindowComponent.appendLine("\t"+requestParam.getParamName());
            }
        }
        List<RequestParam> apiResultParam = param.getApiResultParam();
        if (CollectionUtils.isNotEmpty(apiResultParam)){
            WindowComponent.printLine("------------resultList----------------");
            WindowComponent.append("返回值名");
            WindowComponent.append("\t"+"返回值类型");
            WindowComponent.appendLine("\t"+"返回值说明");
            for (RequestParam requestParam : apiResultParam) {
                WindowComponent.append(getName(requestParam.getParamKey()));
                WindowComponent.append("\t"+ApiParamBuildUtil.getType(requestParam.getParamType()));
                WindowComponent.appendLine("\t"+requestParam.getParamName());
            }
        }
    }

    private static String getName(String fullName){
        if (StringUtils.isBlank(fullName)) return fullName;
        if (fullName.contains(">")) return fullName.substring(fullName.lastIndexOf(">")+1);
        return fullName;
    }

    public static void printInfoLine(String msg){
        WindowComponent.printLine("[INFO] : "+msg);
    }


    public static void printWarnLine(String msg){
        WindowComponent.printLine("[WARN] : "+msg);

    }

    public  static void printError(String msg){
        WindowComponent.printLine("[ERROR] : "+msg);
    }

    private static final List<Character> skipList = new ArrayList<>();
    static {
        skipList.add('\"');
        skipList.add('\'');
        skipList.add('[');
        skipList.add(']');
    }
    private static void printJson(JSONObject jsonObject){
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
//                WindowComponent.appendLine(c);
                blankNum++;
            }else if(c==':'){
                char nextChar = jsonString.charAt(index+1);
                if (nextChar=='{' || nextChar=='['){ //value为JSON时，打印本行，开始下一行。
                    printBlankTable(blankNum);
                    String thisKey = key.toString();
                    WindowComponent.append(getKey(thisKey,lastLevel));
                    WindowComponent.appendLine(" : "+getSubJsonType(nextChar));
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
                printKeyValueLine(blankNum, key, value, c,lastLevel);
                //把标记复位
                type="key";
                key = new StringBuilder();
                value = new StringBuilder();
            }else if(c=='}'){
                printKeyValueLine(blankNum, key, value, ' ',lastLevel);
                if (CollectionUtils.isNotEmpty(lastLevel)){
                    lastLevel.removeLast();
                }
                blankNum--;
//                printBlankTable(blankNum);
//                WindowComponent.appendLine("");
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

    private static void printKeyValueLine(int blankNum, StringBuilder key, StringBuilder value, char c, List<String> lastLevel) {
        printBlankTable(blankNum);
        WindowComponent.append(getKey(key.toString(),lastLevel));
        WindowComponent.append(" : ");
        //打印value时，用$&分割类型和描述，把描述打印在','之后
        String msg = value.toString();
        if (msg.contains("$&")){
            String[] split = msg.split("\\$&");
            WindowComponent.append(split[0]);
            WindowComponent.append("; ");
            WindowComponent.appendLine(split[1]);
        }else{
            WindowComponent.append(msg);
            WindowComponent.appendLine("");
        }
    }

    private static void printBlankTable(int num){
        for (int i =0 ; i<num; i++){
            WindowComponent.append("  ");
        }
    }
}
