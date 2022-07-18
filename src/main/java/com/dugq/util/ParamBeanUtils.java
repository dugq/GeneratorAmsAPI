package com.dugq.util;

import com.alibaba.fastjson.JSONObject;
import com.dugq.pojo.FeignKeyValueBean;
import com.dugq.pojo.KeyValueBean;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.enums.ParamTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by dugq on 2019/12/26.
 */
public class ParamBeanUtils {

    public static JSONObject param2Json(List<ParamBean> params,boolean discardFirstLevel){
        if (CollectionUtils.isEmpty(params)){
            return new JSONObject();
        }
        JSONObject jsonObject = new JSONObject();
        for (ParamBean param : params) {
            if (CollectionUtils.isNotEmpty(param.getChildren())){
                if (discardFirstLevel){
                    jsonObject.putAll(param2Json(param.getChildren(),false));
                }else{
                    jsonObject.put(param.getParamKey(),param2Json(param.getChildren(),false));
                }
            }else{
                jsonObject.put(param.getParamKey(), getParamValue(param));
            }
        }
        return jsonObject;
    }

    public static List<KeyValueBean> param2KVBean(List<ParamBean> params,boolean discardFirstLevel,String parents){
        return params.stream().map(param->{
            List<KeyValueBean> list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(param.getChildren())){
                if (discardFirstLevel){
                    final List<KeyValueBean> children = param2KVBean(param.getChildren(), false, null);
                    list.addAll(children);
                }else{
                    final List<KeyValueBean> children = param2KVBean(param.getChildren(), false,getFullKey(parents,param.getParamKey()));
                    list.addAll(children);
                }
            }else{
                list.add(new KeyValueBean(param.getParamKey(),param.getParamValue(),param.getParamName()));
            }
            return list;
        }).flatMap(List::stream).collect(Collectors.toList());
    }

    public static List<FeignKeyValueBean> param2KVBean(List<ParamBean> params){
        return params.stream().map(param->{
            FeignKeyValueBean feignKeyValueBean = new FeignKeyValueBean();
            feignKeyValueBean.setValueType(param.getParamType().getName());
            feignKeyValueBean.setValue(param.getParamValue());
            feignKeyValueBean.setIndex(param.get$index());
            feignKeyValueBean.setKey(param.getParamKey());
            return feignKeyValueBean;
        }).collect(Collectors.toList());
    }

    public static String getFullKey(String parents,String current){
        if (StringUtils.isBlank(parents)){
            return current;
        }
        if (StringUtils.isBlank(current)){
            return "";
        }
        return parents+"."+current;
    }

    private static Object getParamValue(ParamBean param) {
        return Objects.isNull(param.getParamValue())?"":param.getParamValue();
    }

    private static Object getDefaultValue(ParamTypeEnum paramType) {
        switch (paramType.getType()){
            case 0: return "string";
            case 2: new JSONObject();
            case 3: return 123;
            case 4: return 1.123;
            case 5: return 1.2313;
            case 6: return "12:12:12";
            case 7: return "2022-12-12 12:12:12";
            case 8: return true;
            case 9: return 127;
            case 10: return 65535;
            case 11: return 12321313131L;
            case 12: return new String[]{};
            case 13: return new Object();
            case 14: return -1;
            default: return null;

        }
    }
}
