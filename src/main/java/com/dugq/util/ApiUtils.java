package com.dugq.util;

import com.dugq.bean.GenericTool;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.ThreadStack;
import com.dugq.pojo.enums.ParamTypeEnum;
import com.dugq.util.psidocment.MyMethodUtils;
import com.dugq.util.psidocment.ReturnUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.light.LightParameter;
import com.intellij.psi.javadoc.PsiDocComment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2021/3/23.
 */
public class ApiUtils {

    public static ApiBean getApiParam(Project project, PsiMethod containingMethod, PsiClass containingClass) {
        ApiBean param = new ApiBean();
        param.setApiName(MyMethodUtils.getMethodSimpleDesc(containingMethod));
        //todo 整理方法，实现兼容多个URL和@RequestMapping
        param.setApiURI(MyMethodUtils.getRequestMappings(containingMethod,containingClass));
        param.setApiRequestType(MyMethodUtils.getRequestType(containingMethod));
        param.setApiParamBean(getQueryList(project, containingMethod));
        final List<ParamBean> returnList = getReturnList(project, containingMethod);
        fillReturnData(containingMethod, returnList);
        param.setApiResultParam(returnList);
        return param;
    }


    public static List<ParamBean> getReturnList(Project project, PsiMethod containingMethod) {
        PsiType returnType = containingMethod.getReturnType();
        if (Objects.isNull(returnType)){
            return new ArrayList<>();
        }
        if (MyPsiTypesUtils.isVoid(returnType)){
            return new ArrayList<>();
        }
        PsiParameter returnParam = new LightParameter("返回值",returnType,containingMethod.getNavigationElement());
        ThreadStack.pushVar(returnParam);
        if(MyPsiTypesUtils.isPrimitiveType(returnType) || MyPsiTypesUtils.isDateType(returnType)){
            throw new ErrorException("接口的返回值不应该是基本类型");
        }else if(MyPsiTypesUtils.isCollectionType(returnType,project)
                || MyPsiTypesUtils.isMapType(returnType,project)
                || MyPsiTypesUtils.isArray(returnType)) {
            throw new ErrorException("接口的返回值不应该是集合或者数组类型");
        }else{
            final List<ParamBean> paramListFromPsiType = getParamListFromPsiType(returnType, project, GenericTool.create(returnType));
            ThreadStack.popVar(returnParam);
            return paramListFromPsiType;
        }
    }

    private static void fillReturnData(PsiMethod containingMethod, List<ParamBean> paramListFromPsiType) {
        for (ParamBean paramBean : paramListFromPsiType) {
            if (StringUtils.equals(paramBean.getParamKey(),"data")){
                final String returnDesc = ReturnUtils.getMethodReturnSimpleDesc(containingMethod.getDocComment());
                if (StringUtils.isBlank(returnDesc)){
                    throw new ErrorException("请使用@return 对返回值的data做一个简单的描述来作为字段解释");
                }
                paramBean.setParamName(returnDesc);
            }
        }
    }


    public static List<ParamBean> getQueryList(Project project, PsiMethod containingMethod) {
        List<ParamBean> queryList = new ArrayList<>();
        PsiParameter[] parameters = containingMethod.getParameterList().getParameters();
        for (PsiParameter psiParameter : parameters) {
            ThreadStack.pushVar(psiParameter);
            if(MyPsiTypesUtils.skipParams.contains(psiParameter.getType().getPresentableText())){
                ThreadStack.popVar(psiParameter);
                continue;
            }
            if(MyPsiTypesUtils.skipFiled.contains(psiParameter.getName())){
                ThreadStack.popVar(psiParameter);
                continue;
            }
            final PsiClass parameterClass = MyPsiTypesUtils.getClassByType(psiParameter.getType());
            if (Objects.isNull(parameterClass)){
                throw new ErrorException("class not fount class name = "+psiParameter.getType().getCanonicalText());
            }
            ParamBean query = builderRequestParamsFromParameter(project, psiParameter,containingMethod);
            if(Objects.nonNull(query)){
                queryList.add(query);
            }
            //不放在finally中才能留下犯罪现场
            ThreadStack.popVar(psiParameter);
        }
        return queryList;
    }



    //解析方法的的一个参数对象
    private static ParamBean builderRequestParamsFromParameter(Project project, PsiParameter psiParameter, PsiMethod containingMethod) {
        final PsiType psiType = psiParameter.getType();
        if (MyPsiTypesUtils.isParameterDeprecated(psiParameter)){
            return null;
        }
        ParamBean.validIsNotSupportType(psiType,project);
        //基本类型，构建一个请求参数对象
       if(MyPsiTypesUtils.isPrimitiveOrDateType(psiType)){
           return ParamBean.single(psiParameter.getName(),psiType, containingMethod.getDocComment(), psiParameter);
       }else if(MyPsiTypesUtils.isCollectionType(psiType,project)){
           final PsiType collectionDeepType = MyPsiTypesUtils.getCollectionGenericTypes(psiType,project,psiParameter.getName());
           return ParamBean.build4List(collectionDeepType,project,psiParameter.getName(),containingMethod.getDocComment(),psiParameter);
        }else{ //其他都认为是自定义Object类型
           final ParamBean single = ParamBean.single( psiParameter.getName(), psiType, containingMethod.getDocComment(), psiParameter);
           final List<ParamBean> paramListFromPsiType = getParamListFromPsiType(psiType, project, GenericTool.create(psiType));
           single.setChildren(paramListFromPsiType);
           return single;
        }
    }

    public static List<ParamBean> getParamListFromPsiType(PsiType psiType, Project project, GenericTool genericTypeStack) {
        if(Objects.isNull(psiType)){
            throw new ErrorException("缺少类型指定");
        }
        if (MyPsiTypesUtils.isVoid(psiType)){
            return new ArrayList<>();
        }
        if(MyPsiTypesUtils.isPrimitiveOrDateType(psiType)){ //基本类型,啥也做不了
            throw new ErrorException("不能使用对象解析器解析基本类型");
        }
        if (MyPsiTypesUtils.isCollectionType(psiType, project)||MyPsiTypesUtils.isArray(psiType)){
            throw new ErrorException("接口一般情况我们不建议使用集合或者数组多层嵌套，这会让数据结构过于复杂，而且AMS也无法满足这种数据结构。");
        }
        ArrayList<ParamBean> paramList = new ArrayList<>();
        final PsiClass psiClass = MyPsiTypesUtils.getClassByType(psiType);

        if (Objects.isNull(psiClass)){
            throw new ErrorException("未查找到的类型： class name ="+psiType.getCanonicalText());
        }
        PsiField[] fields = psiClass.getFields();
        for (PsiField field : fields) {
            ThreadStack.pushVar(field);
            if(MyPsiTypesUtils.skipFiled.contains(field.getName())){
                ThreadStack.popVar(field);
                continue;
            }
            final ParamBean paramBean = changeField2RequestParam(field,genericTypeStack);
            if (Objects. nonNull(paramBean)){
                paramList.add(paramBean);
            }
            //不放在finally中，这样不执行以保证留下犯罪现场。
            ThreadStack.popVar(field);
        }

        final List<ParamBean> supperParamList = getParamBeansFromSupperClass(project, psiClass,genericTypeStack);
        if (CollectionUtils.isNotEmpty(supperParamList)){
            paramList.addAll(supperParamList);
        }
        return paramList;
    }

    @Nullable
    private static List<ParamBean> getParamBeansFromSupperClass(Project project, PsiClass psiClass, GenericTool genericTypeStack) {
        final PsiClassType[] extendsListType = psiClass.getExtendsListTypes();
        if (ArrayUtils.isEmpty(extendsListType) || StringUtils.equals(MyPsiTypesUtils.getClassByType(psiClass.getExtendsListTypes()[0]).getQualifiedName(),"java.lang.Object")){
            return null;
        }
        final PsiClassType superPsiType = extendsListType[0];
        return getParamListFromPsiType(superPsiType, project,genericTypeStack.explainSupperClass(superPsiType,project));
    }

    private static ParamBean changeField2RequestParam(PsiField field, GenericTool extGenericTypes) {
        if (Objects.isNull(field)){
            return null;
        }
        final String filedName = field.getName();
        final PsiDocComment filedDocComment = field.getDocComment();

        PsiType filedType = extGenericTypes.getPsiTypeAndDealGeneric(field);

        if (MyPsiTypesUtils.isVoid(filedType)){
            return null;
        }

        if (MyPsiTypesUtils.isPrimitiveOrDateType(filedType)){
            return ParamBean.single(filedName,filedType,field.getDocComment(),field);
        }
        if (MyPsiTypesUtils.isCollectionType(filedType, field.getProject())){
            final PsiType collectionDeepType = MyPsiTypesUtils.getCollectionGenericTypes(filedType, field.getProject(),filedName);
            return ParamBean.build4List(collectionDeepType, field.getProject(),filedName,filedDocComment,field);
        }
        if (MyPsiTypesUtils.isArray(filedType)){
            final PsiType collectionDeepType = MyPsiTypesUtils.getArrayType(filedType);
            return ParamBean.build4List(collectionDeepType, field.getProject(),filedName,filedDocComment,field);
        }

        //其他都认为是Object类型
        final ParamBean paramsBean = ParamBean.single(filedName,filedType,filedDocComment,field);
        paramsBean.setParamType(ParamTypeEnum.OBJECT);
        final List<ParamBean> paramListFromPsiType = getParamListFromPsiType(filedType, field.getProject(), GenericTool.create(filedType));
        paramsBean.setChildren(paramListFromPsiType);
        return paramsBean;
    }

}
