package com.dugq.pojo;

import com.dugq.bean.GenericTool;
import com.dugq.exception.ErrorException;
import com.dugq.exception.ParameterDocException;
import com.dugq.pojo.ams.ParamSelectValue;
import com.dugq.pojo.enums.ParamTypeEnum;
import com.dugq.util.APIPrintUtil;
import com.dugq.util.ApiUtils;
import com.dugq.util.MyPsiTypesUtils;
import com.dugq.util.psidocment.DocumentUtil;
import com.dugq.util.psidocment.ParameterUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJvmModifiersOwner;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2019/12/25.
 */
public class ParamBean {
    /**
     * 是否非空
     * 0：true
     * 1：false
     */
    private Integer paramNotNull = 1;
    /**
     * 参数说明
     */
    private String paramName;
    /**
     * 参数名
     */
    private String paramKey;

    /**
     * 事例
     */
    private String paramValue;

    private ParamTypeEnum paramType;

    private String paramLimit;
    /**
     * 详情
     */
    private String paramNote;

    /**
     * 可能值
     */
    private List<ParamSelectValue> paramValueList;

    /**
     * 顺序
     */
    private Integer $index;

    private List<ParamBean> children;

    private ParamBean parentBean;

    private ParamTypeEnum childType;

    public ParamBean getParentBean() {
        return parentBean;
    }

    public void setParentBean(ParamBean parentBean) {
        this.parentBean = parentBean;
    }

    public String getFullParamKeys(){
        if (Objects.isNull(parentBean)){
            return this.paramKey;
        }
        return this.parentBean.getFullParamKeys() +"."+this.paramKey;
    }

    public List<ParamBean> getChildren() {
        return children;
    }

    public void setChildren(List<ParamBean> children) {
        this.children = children;
    }

    public Integer getParamNotNull() {
        return paramNotNull;
    }

    public void setParamNotNull(Integer paramNotNull) {
        this.paramNotNull = paramNotNull;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public ParamTypeEnum getParamType() {
        return paramType;
    }

    public void setParamType(ParamTypeEnum paramType) {
        this.paramType = paramType;
    }

    public String getParamLimit() {
        return paramLimit;
    }

    public void setParamLimit(String paramLimit) {
        this.paramLimit = paramLimit;
    }

    public String getParamNote() {
        return paramNote;
    }

    public void setParamNote(String paramNote) {
        this.paramNote = paramNote;
    }

    public List<ParamSelectValue> getParamValueList() {
        return paramValueList;
    }

    public void setParamValueList(List<ParamSelectValue> paramValueList) {
        this.paramValueList = paramValueList;
    }

    public Integer get$index() {
        return $index;
    }

    public void set$index(Integer $index) {
        this.$index = $index;
    }


    public static ParamBean single(String paramName, PsiType psiType, PsiDocComment psiDocComment, PsiJvmModifiersOwner psiJvmModifiersOwner){
        validatorDoc(psiDocComment,psiJvmModifiersOwner,paramName);
        ParamBean query = new ParamBean();
        query.setParamKey(paramName);
        query.setParamType(ParamTypeEnum.getTypeByPsiType(psiType,psiJvmModifiersOwner,psiJvmModifiersOwner.getProject()));
        query.setParamName(ParameterUtils.getVarSimpleDescFromDocComment(paramName, psiDocComment));
        query.setParamValue(ParameterUtils.getVarExampleValue(paramName, psiDocComment));
        query.setParamValueList(ParameterUtils.getSelectValuesFromDoc(psiDocComment,paramName));
        query.setParamNotNull(MyPsiTypesUtils.isJvmModifiersOwnerNotNull(psiJvmModifiersOwner)?0:1);
        return query;
    }


    private static void validatorDoc(PsiDocComment psiDocComment, PsiJvmModifiersOwner psiJvmModifiersOwner, String paramName) {
        if (psiJvmModifiersOwner instanceof PsiParameter){
            if (Objects.isNull(psiDocComment)){
                throw new ParameterDocException("接口注释请认真书写！");
            }
            final String desc = ParameterUtils.getVarSimpleDescFromDocComment(paramName, psiDocComment);
            if (StringUtils.isBlank(desc)){
                throw new ParameterDocException("请在接口注释中认真书写字段描述");
            }
            return;
        }
        if (Objects.isNull(psiDocComment)){
            if (Objects.nonNull(psiJvmModifiersOwner) && MyPsiTypesUtils.isElementInLiberay(psiJvmModifiersOwner)){
                if (DocumentUtil.defaultPropsMap.containsKey(paramName)){
                    return;
                }
                APIPrintUtil.printWarnLine("jar包字段【"+ThreadStack.getStack()+"】的注释无法读取,自行补充",psiJvmModifiersOwner.getProject());
            }else{
                throw new ParameterDocException("参数【"+paramName+"】的注释无法读取,请书写注释！");
            }
        }
    }

    public static ParamBean build4List(PsiType collectionDeepType, Project project, String paramKey, PsiDocComment psiDocComment, PsiJvmModifiersOwner psiJvmModifiersOwner){
        if (MyPsiTypesUtils.isCollectionType(collectionDeepType,project)){
            throw new ErrorException(null,null,"多维数组在AMS中无法表达，请您自行解决！");
        }else if(MyPsiTypesUtils.isPrimitiveOrDateType(collectionDeepType)){
            final ParamBean request = single(paramKey,collectionDeepType,psiDocComment,psiJvmModifiersOwner);
            //重新修正基本类型参数为数组
            request.setChildType(request.getParamType());
            request.setParamType(ParamTypeEnum.ARRAY);
            return request;
        }else{
               /*
                    其他都认定为自定义Object
                    对于List<Object> p 定义的参数，解析为： p 为数组，然后以p.* 穷举所有属性
                */
            //然后重置参数类型
            final ParamBean paramsBean = single(paramKey,collectionDeepType,psiDocComment,psiJvmModifiersOwner);
            paramsBean.setParamType(ParamTypeEnum.ARRAY);
            paramsBean.setChildType(ParamTypeEnum.OBJECT);
            final List<ParamBean> paramListFromClass = ApiUtils.getParamListFromPsiType(collectionDeepType, project, GenericTool.create(collectionDeepType));
            paramsBean.setChildren(paramListFromClass);
            return paramsBean;
        }
    }

    public static void validIsNotSupportType( PsiType psiType, Project project){
        if (MyPsiTypesUtils.isArray(psiType) || MyPsiTypesUtils.isMapType(psiType,project)){
            throw new ErrorException(null,null,"参数不建议使用Map类型，请自定义对象。");
        }
    }


    public void removeChild(ParamBean paramBean) {
        this.children.remove(paramBean);
    }

    public ParamTypeEnum getChildType() {
        return childType;
    }

    public void setChildType(ParamTypeEnum childType) {
        this.childType = childType;
    }
}


