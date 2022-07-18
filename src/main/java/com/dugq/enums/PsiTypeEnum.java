package com.dugq.enums;

/**
 * Created by dugq on 2021/4/20.
 */
public enum  PsiTypeEnum {
    JSON("json","Object对象统称为json"),
    ARRAY("array","数组"),
    DATE("date","时间类型。包含time，localXXXX"),
    ;

    private String type;

    private String desc;

    PsiTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
