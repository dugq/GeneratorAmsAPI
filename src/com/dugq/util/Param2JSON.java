package com.dugq.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dugq.pojo.RequestParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dugq on 2019/12/26.
 */
public class Param2JSON {

    public static JSONObject param2Json(List<RequestParam> params){
        if(CollectionUtils.isEmpty(params)){
            return new JSONObject();
        }
        Map<String, List<RequestParam>> map = params.stream().collect(Collectors.groupingBy(param -> {
            String paramKey = param.getParamKey();
            if (!paramKey.contains(">>")) {
                return "111";
            } else {
                String[] split = paramKey.split(">>");
                return split[split.length - 2];
            }
        }));
        List<RequestParam> requestParams = map.get("111");
        JSONObject result = getJsonObject(map, requestParams);
        return result;
    }

    @NotNull
    private static JSONObject getJsonObject(Map<String, List<RequestParam>> map, List<RequestParam> requestParams) {
        JSONObject result = new JSONObject();
        for (RequestParam requestParam : requestParams) {
            String paramKey = requestParam.getParamKey();
            String shortName ;
            if(paramKey.contains(">>")){
                String[] split = paramKey.split(">>");
                shortName= split[split.length-1];
            }else{
                shortName = paramKey;
            }
            List<RequestParam> children = map.get(shortName);
            if(CollectionUtils.isEmpty(children)){
                result.put(shortName,StringUtils.isBlank(requestParam.getParamValue())?getDefaultValue(requestParam.getParamValue(),requestParam.getParamType()):requestParam.getParamValue());
            }else{
                if(requestParam.getParamType()==12){
                    JSONArray array = new JSONArray();
                    array.add(getJsonObject(map,children));
                    result.put(shortName,array);
                }else{
                    result.put(shortName,getJsonObject(map,children));
                }
            }
        }
        return result;
    }

    private static Object getDefaultValue(String paramValue, Integer type){
        if(StringUtils.isNotEmpty(paramValue)){
            return paramValue;
        }
        if(type==0){
            return "";
        }
        if(type == 2){
            return new JSONObject();
        }
        if(type == 3){
            return 0;
        }
        if(type ==4){
            return 0.0;
        }
        if(type == 5){
            return 0.0;
        }
        if(type == 6){
            return new Date();
        }
        if(type == 8){
            return false;
        }
        if(type ==9){
            return 0;
        }
        if(type == 10){
            return 0;
        }
        if(type == 11){
            return 0;
        }
        if(type == 12){
            return new ArrayList<>();
        }
        return new JSONObject();
    }
}
