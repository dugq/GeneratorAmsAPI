package com.dugq.requestmapping.param.bean;

import com.alibaba.fastjson.JSONObject;
import com.dugq.pojo.ParamBean;
import com.intellij.psi.PsiType;

/**
 * Created by dugq on 2021/4/19.
 */
public class RpcParamBean extends ParamBean {
    //参数位置
    private Integer index;
    //参数名称
    private String name;
    //参数是否是基本类型
    private boolean primitive;
    //参数具体类型的间断描述
    private String shortType;
    //参数具体类型
    private PsiType paramPsiType;

    //数组类型（包含array list set）的子类型
    private String childShortType;
    //数组类型（包含array list set）的子类型是否是基本类型
    private boolean childPrimitive;
    //当type==json的时候，会把请求体变更为json存储。key为object filed name value为默认值
    private JSONObject jsonBody;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public String getShortType() {
        return shortType;
    }

    public void setShortType(String shortType) {
        this.shortType = shortType;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getChildShortType() {
        return childShortType;
    }

    public void setChildShortType(String childShortType) {
        this.childShortType = childShortType;
    }


    public boolean isChildPrimitive() {
        return childPrimitive;
    }

    public void setChildPrimitive(boolean childPrimitive) {
        this.childPrimitive = childPrimitive;
    }

    public JSONObject getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(JSONObject jsonBody) {
        this.jsonBody = jsonBody;
    }

    public PsiType getParamPsiType() {
        return paramPsiType;
    }

    public void setParamPsiType(PsiType paramPsiType) {
        this.paramPsiType = paramPsiType;
    }
}
