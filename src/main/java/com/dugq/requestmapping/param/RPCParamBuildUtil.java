package com.dugq.requestmapping.param;

import com.alibaba.fastjson.JSONObject;
import com.dugq.bean.GenericTool;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.enums.ParamTypeEnum;
import com.dugq.util.MyPsiTypesUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
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
        if (ArrayUtils.isEmpty(parameters)){
            return Collections.emptyList();
        }
        AtomicInteger index = new AtomicInteger();
        return Stream.of(parameters).map(parameter-> builderParamBeanByParameter(project, index, parameter)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Nullable
    private static ParamBean builderParamBeanByParameter(Project project, AtomicInteger index, PsiParameter parameter) {
        PsiType parameterType = parameter.getType();
        if(MyPsiTypesUtils.isSkipType(parameterType)){
            return null;
        }
        ParamBean paramBean = new ParamBean();
        paramBean.setParamKey(parameter.getName());
        paramBean.setParamName(parameter.getName());
        paramBean.set$index(index.getAndIncrement());
        if(MyPsiTypesUtils.isPrimitiveType(parameterType)){
            paramBean.setParamType(ParamTypeEnum.getTypeByPsiType(parameterType,null,project));
        }else if (MyPsiTypesUtils.isMapType(parameterType, project)){
            paramBean.setParamType(ParamTypeEnum.JSON);
        }else if (MyPsiTypesUtils.isCollectionType(parameterType, project) || MyPsiTypesUtils.isArray(parameterType)){
            paramBean.setParamType(ParamTypeEnum.ARRAY);
        }else{
            paramBean.setParamType(ParamTypeEnum.JSON);
            JSONObject jsonBody = buildJson(parameterType, project, GenericTool.create(parameterType));
            paramBean.setParamValue(jsonBody.toJSONString());
        }
        return paramBean;
    }

    public static JSONObject buildJson(PsiType psiType, Project project, GenericTool genericTypeStack) {
        JSONObject jsonObject = new JSONObject();
        final PsiClass psiClass = MyPsiTypesUtils.getClassByType(psiType);
        if (Objects.isNull(psiClass)){
            throw new ErrorException("未查找到的类型： class name ="+psiType.getCanonicalText());
        }
        PsiField[] fields = psiClass.getFields();
        for (PsiField field : fields) {
            if(MyPsiTypesUtils.skipFiled.contains(field.getName())){
                continue;
            }
            if(MyPsiTypesUtils.isPrimitiveType(field.getType()) || MyPsiTypesUtils.isDateType(field.getType())){
                jsonObject.put(field.getName(),"");
            }else if (MyPsiTypesUtils.isMapType(field.getType(), project)){
                jsonObject.put(field.getName(),"{}");
            }else if (MyPsiTypesUtils.isCollectionType(field.getType(), project) || MyPsiTypesUtils.isArray(field.getType())){
                jsonObject.put(field.getName(),"[]");
            }else{
                jsonObject.put(field.getName(),buildJson(field.getType(),project,genericTypeStack));
            }
        }
        final JSONObject parentObject = getParamBeansFromSupperClass(project, psiClass,genericTypeStack);
        if (MapUtils.isNotEmpty(parentObject)){
            jsonObject.putAll(parentObject);
        }
        return jsonObject;
    }

    @Nullable
    private static JSONObject getParamBeansFromSupperClass(Project project, PsiClass psiClass, GenericTool genericTypeStack) {
        final PsiClassType[] extendsListType = psiClass.getExtendsListTypes();
        if (ArrayUtils.isEmpty(extendsListType) || StringUtils.equals(MyPsiTypesUtils.getClassByType(psiClass.getExtendsListTypes()[0]).getQualifiedName(),"java.lang.Object")){
            return null;
        }
        final PsiClassType superPsiType = extendsListType[0];
        return buildJson(superPsiType, project,genericTypeStack.explainSupperClass(superPsiType,project));
    }

}
