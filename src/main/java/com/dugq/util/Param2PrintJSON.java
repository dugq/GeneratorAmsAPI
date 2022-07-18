package com.dugq.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.enums.ParamTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by dugq on 2019/12/26.
 */
public class Param2PrintJSON {

    public static JSONObject param2Json(List<ParamBean> params){
        if(CollectionUtils.isEmpty(params)){
            return new JSONObject();
        }
        Map<String, List<ParamBean>> map = params.stream().collect(Collectors.groupingBy(param -> {
            String paramKey = param.getParamKey();
            if (!paramKey.contains(">>")) {
                return "111";
            } else {
                String[] split = paramKey.split(">>");
                return split[split.length - 2];
            }
        }));
        List<ParamBean> paramBeans = map.get("111");
        return getJsonObject(map, paramBeans);
    }

    @NotNull
    private static JSONObject getJsonObject(Map<String, List<ParamBean>> map, List<ParamBean> paramBeans) {
        JSONObject result = new JSONObject();
        for (ParamBean paramBean : paramBeans) {
            String paramKey = paramBean.getParamKey();
            String shortName ;
            if(paramKey.contains(">>")){
                String[] split = paramKey.split(">>");
                shortName= split[split.length-1];
            }else{
                shortName = paramKey;
            }
            List<ParamBean> children = map.get(shortName);
            if(CollectionUtils.isEmpty(children)){
                result.put(shortName, getJSONValue(paramBean));
            }else{
                if(Objects.equals(paramBean.getParamType(), ParamTypeEnum.ARRAY)){
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

    @NotNull
    private static String getJSONValue(ParamBean paramBean) {
        String value = "";
        value += paramBean.getParamType().getName();
        if (StringUtils.isNotBlank(paramBean.getParamName())){
            value = value+"$&"+ paramBean.getParamName();
        }
        return value;
    }


}
