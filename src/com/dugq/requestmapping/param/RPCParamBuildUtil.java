package com.dugq.requestmapping.param;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dugq.enums.PsiTypeEnum;
import com.dugq.exception.StopException;
import com.dugq.requestmapping.param.bean.ParamBean;
import com.dugq.util.ErrorPrintUtil;
import com.dugq.util.MyPsiTypesUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dugq on 2021/4/19.
 */
public class RPCParamBuildUtil {

    private RPCParamBuildUtil(){

    }

    public static List<ParamBean> getList(PsiMethod psiMethod, Project project){
        PsiParameterList parameterList = psiMethod.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        AtomicInteger index = new AtomicInteger();
        return Stream.of(parameters).map(parameter->{
            ParamBean paramBean = new ParamBean();
            PsiType parameterType = parameter.getType();
            if(MyPsiTypesUtils.isSkipType(parameterType)){
                return null;
            }
            if(MyPsiTypesUtils.isSkipFiled(parameter.getName())){
                return null;
            }
            else if(MyPsiTypesUtils.isPrimitiveType(parameterType)){
                paramBean.setPrimitive(true);
                paramBean.setShortType(MyPsiTypesUtils.getPresentType(parameterType));
            }else if (MyPsiTypesUtils.isMap(parameterType,project)){
                ErrorPrintUtil.printLine("请不要以Map作为参数！如果你非要用，那就不要使用此插件。",project);
                throw new StopException();
            }else if (MyPsiTypesUtils.isCollection(parameterType,project) || MyPsiTypesUtils.isArray(parameterType)){
                paramBean.setShortType(PsiTypeEnum.ARRAY.getType());
                PsiType childTypes = MyPsiTypesUtils.getChildTypes(parameterType,project);
                if (Objects.isNull(childTypes)){
                    ErrorPrintUtil.printLine("参数:【"+parameter.getName()+"】 没有指定泛型",project);
                    throw new StopException();
                }
                if (MyPsiTypesUtils.isPrimitiveType(childTypes)){
                     paramBean.setChildPrimitive(true);
                     paramBean.setChildShortType(MyPsiTypesUtils.getPresentType(childTypes));
                }else if(MyPsiTypesUtils.isArray(childTypes) || MyPsiTypesUtils.isCollection(childTypes,project) || MyPsiTypesUtils.isMap(childTypes,project)){
                    ErrorPrintUtil.printLine("参数【"+parameter.getName()+"】字段，此插件暂不支持多维数组格式！",project);
                    throw new StopException();
                }else{
                    paramBean.setChildPrimitive(false);
                    paramBean.setChildShortType(PsiTypeEnum.JSON.getType());
                    JSONObject jsonBody = buildJson(childTypes, project, parameter.getName());
                    paramBean.setJsonBody(jsonBody);
                }
                paramBean.setChildType(childTypes);
            }else{
                paramBean.setShortType(PsiTypeEnum.JSON.getType());
                JSONObject jsonBody = buildJson(parameterType, project, parameter.getName());
                paramBean.setJsonBody(jsonBody);
            }
            paramBean.setName(parameter.getName());
            paramBean.setIndex(index.getAndIncrement());
            paramBean.setParamPsiType(parameterType);
            return paramBean;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static JSONObject buildJson(PsiType psiType, Project project,String parentNames) {
        JSONObject result = new JSONObject();
        PsiClass classByType = MyPsiTypesUtils.getClassByType(psiType, project);
        PsiField[] allFields = classByType.getAllFields();
        for (PsiField psiField : allFields) {
            PsiType fieldType = psiField.getType();
            if (MyPsiTypesUtils.isSkipType(fieldType) || MyPsiTypesUtils.isSkipFiled(psiField.getName())){
                continue;
            }else if((MyPsiTypesUtils.isPrimitiveType(fieldType))){
                result.put(psiField.getName(),MyPsiTypesUtils.getDefaultValue(fieldType));
            }else if(MyPsiTypesUtils.isArray(psiType) || MyPsiTypesUtils.isCollection(fieldType,project)){
                JSONArray array = new JSONArray();
                PsiType childTypes = MyPsiTypesUtils.getChildTypes(fieldType,project);
                if (Objects.isNull(childTypes)){
                    ErrorPrintUtil.printLine("参数:【"+parentNames+"."+psiField.getName()+"】 没有指定泛型",project);
                    throw new StopException();
                }
                if (MyPsiTypesUtils.isPrimitiveType(childTypes)){
                    array.add(MyPsiTypesUtils.getDefaultValue(fieldType));
                }else if(MyPsiTypesUtils.isArray(childTypes) || MyPsiTypesUtils.isCollection(childTypes,project) || MyPsiTypesUtils.isMap(childTypes,project)){
                    ErrorPrintUtil.printLine("参数【"+parentNames+"."+psiField.getName()+"】字段，此插件暂不支持多维数组格式！",project);
                    throw new StopException();
                }else{
                    array.add(buildJson(fieldType,project,parentNames+"."+psiField.getName()));
                }
                result.put(psiField.getName(), array);
            }else if(MyPsiTypesUtils.isMap(fieldType,project)){
                ErrorPrintUtil.printLine("【"+parentNames+"."+psiField.getName()+"】字段是Map，不支持！",project);
                throw new StopException();
            }else{//其他都是Object
                result.put(psiField.getName(),buildJson(fieldType,project,parentNames+"."+psiField.getName()));
            }
        }
        return result;
    }

}
