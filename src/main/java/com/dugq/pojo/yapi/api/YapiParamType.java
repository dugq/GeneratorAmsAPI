package com.dugq.pojo.yapi.api;

import com.dugq.exception.ErrorException;
import com.dugq.pojo.enums.ParamTypeEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * API参数类型枚举
 * @author dugq
 * @date 2021/8/13 2:06 下午
 */
public enum YapiParamType {
    STRING("string"),
    BOOLEAN("boolean"),
    ARRAY("array"),
    OBJECT("object"),
    NUMBER("number"),
    Integer("integer"),
    ;
    private final String name;

    YapiParamType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private  static final List<ParamTypeEnum> NUMBER_LIST = Arrays.asList(
            ParamTypeEnum.FLOAT,
            ParamTypeEnum.DOUBLE,
            ParamTypeEnum.TIME,
            ParamTypeEnum.DATE_TIME,
            ParamTypeEnum.BYTE,
            ParamTypeEnum.SHORT,
            ParamTypeEnum.LONG,
            ParamTypeEnum.NUMBER
            );


    public static YapiParamType getTypeParamType(ParamTypeEnum paramTypeEnum){
        if (Objects.equals(ParamTypeEnum.STRING,paramTypeEnum)){
            return STRING;
        }
        if (Objects.equals(ParamTypeEnum.BOOLEAN,paramTypeEnum)){
            return BOOLEAN;
        }
        if (Objects.equals(ParamTypeEnum.ARRAY,paramTypeEnum)){
            return ARRAY;
        }
        if (Objects.equals(ParamTypeEnum.OBJECT,paramTypeEnum)){
            return OBJECT;
        }
        if (Objects.equals(ParamTypeEnum.INT,paramTypeEnum)){
            return Integer;
        }
        if (NUMBER_LIST.contains(paramTypeEnum)){
            return NUMBER;
        }
        throw new ErrorException("不支持的参数类型:"+paramTypeEnum.getName());
    }
}
