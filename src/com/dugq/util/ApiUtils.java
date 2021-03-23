package com.dugq.util;

import com.dugq.ams.ApiEditorService;
import com.dugq.component.SelectInputComponent;
import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.EditorParam;
import com.dugq.pojo.GroupVo;
import com.dugq.pojo.RequestParam;
import com.dugq.pojo.SimpleApiVo;
import com.dugq.pojo.enums.RequestType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by dugq on 2021/3/23.
 */
public class ApiUtils {

    public static EditorParam getApiParam( Project project, PsiMethod containingMethod, PsiClass containingClass) {
        EditorParam param = new EditorParam();
        String mapping = getRequestUrl(containingClass, SpringMVCConstant.RequestMapping,project);
        if(Objects.isNull(mapping)){
            throw new ErrorException(containingMethod,null,"class的@requestMapping注解呢？？？");
        }
        String subMapping = getRequestUrl(containingMethod, SpringMVCConstant.GetMapping, project);
        String requestMethod;
        if(Objects.nonNull(subMapping)){
            requestMethod = "get";
        }else{
            subMapping = getRequestUrl(containingMethod, SpringMVCConstant.PostMapping, project);
            if(Objects.isNull(subMapping)){
                throw new ErrorException(containingMethod,null,"方法的 getMapping or postMapping 注解呢？？？");
            }
            requestMethod = "post";
        }
        String uri = mapping+(subMapping.startsWith("/")?"":"/")+subMapping;
        param.setApiRequestType(RequestType.getByDesc(requestMethod).getType());
        param.setApiURI(uri);
        //读取方法注释
        PsiDocComment docComment = containingMethod.getDocComment();
        String methodDesc = DesUtil.getFiledDesc(docComment);
        //截取第一段作为接口注释
        if(methodDesc.contains("@")){
            methodDesc = methodDesc.substring(0,methodDesc.indexOf("@"));
        }
        if(StringUtils.isBlank(methodDesc)){
            throw new ErrorException(containingMethod,null,"接口名称请用在方法注释中声明");
        }
        param.setApiName(DesUtil.trimFirstAndLastChar(methodDesc,','));
        List<RequestParam> queryList = getQueryList(project, containingMethod);
        param.setApiRequestParam(queryList);
        List<RequestParam> returnList = getReturnList(project, containingMethod);
        param.setApiResultParam(returnList);
        param.setApiSuccessMock(Param2JSON.param2Json(returnList).toJSONString());
        List<SimpleApiVo> simpleApiVos = getSimpleApiVos(project, uri);
        if(CollectionUtils.isNotEmpty(simpleApiVos)){
            if(simpleApiVos.size()>1){
                throw new ErrorException(containingMethod,null,"存在多个相同URI的API，无法添加！！！");
            }
            int update = Messages.showDialog("请选择是否更新接口:"+uri, "存在相同uri接口，是否更新？", new String[]{"是", "否"}, 0, null);
            if (update==0){
                SimpleApiVo simpleApiVo = simpleApiVos.get(0);
                param.setGroupID(simpleApiVo.getGroupID());
                param.setApiID(simpleApiVo.getApiID());
                param.setType(1);
                return param;
            }else{
                throw new ErrorException(containingMethod,null,"存在同名接口"+uri+"，无法添加！！！");
            }

        }
        param.setType(2);
        return param;
    }

    private static List<SimpleApiVo> getSimpleApiVos(Project project, String uri) {
        List<SimpleApiVo> simpleApiVos = ApiEditorService.amsApiSearchParam(project, uri);
        if (Objects.isNull(simpleApiVos)){
            return Collections.emptyList();
        }
        return simpleApiVos.stream().filter(vo->StringUtils.equals(vo.getApiURI(),uri)).collect(Collectors.toList());
    }

    public static GroupVo getGroupVo(List<GroupVo> groupVos, String uri) {
        SelectInputComponent comboBox = new SelectInputComponent(groupVos,uri);
        if(!comboBox.showAndGet()){
            throw new StopException();
        }
        GroupVo groupVo = groupVos.get(comboBox.getBox().getSelectedIndex());
        if(CollectionUtils.isNotEmpty(groupVo.getChildGroupList())){
            return getGroupVo(groupVo.getChildGroupList(), uri);
        }
        return groupVo;
    }

    private static List<RequestParam> getReturnList(Project project, PsiMethod containingMethod) {
        List<RequestParam> returnList = new ArrayList<>();
        PsiType returnType = containingMethod.getReturnType();
        if(Objects.nonNull(returnType)){
            if(returnType instanceof PsiPrimitiveType){
                RequestParam returnValue = new RequestParam();
                returnValue.setParamKey(((PsiPrimitiveType) returnType).getName());
                returnValue.setParamName(DesUtil.getReturn(containingMethod));
                returnValue.setParamType(ApiParamBuildUtil.getType(returnType.getPresentableText()));
                returnList.add(returnValue);
            }
            else if(NormalTypes.isNormalType(returnType.getPresentableText())){
                RequestParam returnValue = new RequestParam();
                returnValue.setParamKey(returnType.getPresentableText());
                returnValue.setParamName(DesUtil.getReturn(containingMethod));
                returnValue.setParamType(ApiParamBuildUtil.getType(returnType.getPresentableText()));
                returnList.add(returnValue);
            }else if(returnType.getPresentableText().startsWith("List")
                    || returnType.getPresentableText().startsWith("Map")
                    || returnType.getPresentableText().startsWith("Set")
                    || returnType instanceof PsiArrayType) {
                throw new ErrorException(null,null,"返回值不支持直接返回集合");
            }else{
                String canonicalText = returnType.getCanonicalText();
                PsiClass returnClass;
                List<String> childClass = new ArrayList<>();
                if(canonicalText.contains("<")){
                    String[] classNames = canonicalText.split("<");
                    String fieldType = classNames[0];
                    for (int i = 1 ; i<classNames.length;i++ ) {
                        childClass.add(classNames[i]);
                    }
                    returnClass = JavaPsiFacade.getInstance(project).findClass(fieldType, GlobalSearchScope.allScope(project));
                }else{
                    returnClass = JavaPsiFacade.getInstance(project).findClass(canonicalText, GlobalSearchScope.allScope(project));
                }
                for (PsiField field : returnClass.getFields()) {
                    List<RequestParam> paramListFromFiled = ApiParamBuildUtil.getParamListFromFiled(field, project, null, field.getName(), childClass);
                    returnList.addAll(paramListFromFiled);
                }
            }
        }
        return returnList;
    }


    private static List<RequestParam> getQueryList(Project project, PsiMethod containingMethod) {
        List<RequestParam> queryList = new ArrayList<>();
        PsiParameter[] parameters = containingMethod.getParameterList().getParameters();
        for (PsiParameter psiParameter : parameters) {
            if(NormalTypes.skipParams.contains(psiParameter.getType().getPresentableText())){
                continue;
            }
            if(NormalTypes.skipFiled.contains(psiParameter.getName())){
                continue;
            }
            List<RequestParam> query = getParam(project, psiParameter,containingMethod);
            queryList.addAll(query);
        }
        return queryList;
    }

    private static List<RequestParam> getParam(Project project, PsiVariable psiParameter, PsiMethod containingMethod) {
        PsiType psiType = psiParameter.getType();
        if(psiType instanceof PsiPrimitiveType){
            //如果是基本类型
            RequestParam query = new RequestParam();
            query.setParamKey(psiParameter.getName());
            query.setParamType(ApiParamBuildUtil.getType(psiType.getPresentableText()));
            query.setParamName(DesUtil.getParamDesc(containingMethod,psiParameter.getName()));
            query.setParamValue(DesUtil.getParamExp(containingMethod,psiParameter.getName()));
            query.setParamValueList(DesUtil.getParamEnumValues(containingMethod,psiParameter.getName(),project));
            PsiAnnotation notNull = psiParameter.getAnnotation("javax.validation.constraints.NotNull");
            if(Objects.nonNull(notNull)){
                query.setParamNotNull(0);
            }
            return ApiParamBuildUtil.singletonList(query);
        }else if(NormalTypes.isNormalType(psiType.getPresentableText())){
            //如果是包装类型
            RequestParam query = new RequestParam();
            query.setParamKey(psiParameter.getName());
            query.setParamType(ApiParamBuildUtil.getType(psiType.getPresentableText()));
            query.setParamName(DesUtil.getParamDesc(containingMethod,psiParameter.getName()));
            query.setParamValue(DesUtil.getParamExp(containingMethod,psiParameter.getName()));
            query.setParamValueList(DesUtil.getParamEnumValues(containingMethod,psiParameter.getName(),project));
            PsiAnnotation notNull = psiParameter.getAnnotation("javax.validation.constraints.NotNull");
            if(Objects.nonNull(notNull)){
                query.setParamNotNull(0);
            }
            return ApiParamBuildUtil.singletonList(query);
        }else if(psiType.getPresentableText().startsWith("List") || psiType.getPresentableText().startsWith("Set")){
            String[] types= psiType.getCanonicalText().split("<");
            if(types.length>1){
                String childPackage=types[1].split(">")[0];
                RequestParam query = new RequestParam();
                query.setParamKey(psiParameter.getName());
                query.setParamType(ApiParamBuildUtil.getType(childPackage));
                query.setParamName(DesUtil.getParamDesc(containingMethod,psiParameter.getName()));
                List<RequestParam> requstParams = ApiParamBuildUtil.singletonList(query);
                PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(childPackage, GlobalSearchScope.allScope(project));
                requstParams.addAll(ApiParamBuildUtil.getParamListFromClass(psiClassChild,project,null,psiParameter.getName(), Arrays.asList(types)));
                return requstParams;
            }else{
                throw new ErrorException(containingMethod,null,"参数"+psiParameter.getName()+"未加泛型");
            }
        }else if(psiType.getPresentableText().startsWith("Map")){
            ApiParamBuildUtil.error("参数不支持Map",project);
            throw new ErrorException(containingMethod,null,"参数不支持Map");
        }else{ //object
            List<String> types = new ArrayList<>();
            PsiClass psiClassChild;
            if(psiType.getCanonicalText().contains("<")){
                String[] classNames = psiType.getCanonicalText().split("<");
                String fieldType = classNames[0];
                for (int i = 1 ; i<classNames.length;i++ ) {
                    types.add(classNames[i]);
                }
                psiClassChild = JavaPsiFacade.getInstance(project).findClass(fieldType, GlobalSearchScope.allScope(project));
            }else{
                psiClassChild = JavaPsiFacade.getInstance(project).findClass(psiType.getCanonicalText(), GlobalSearchScope.allScope(project));
            }
            return ApiParamBuildUtil.getParamListFromClass(psiClassChild,project,null,null, types);
        }
    }

    private String getType(String presentableText) {
        if(presentableText.contains("Integer") || StringUtils.equals(presentableText,"int")){
            return "int";
        }
        if(presentableText.contains("Long") || StringUtils.equals(presentableText,"long")){
            return "long";
        }
        if(presentableText.contains("Byte") || StringUtils.equals(presentableText,"byte")){
            return "byte";
        }
        if(presentableText.contains("String") || StringUtils.equals(presentableText,"String")){
            return "string";
        }
        if(presentableText.contains("Double") || StringUtils.equals(presentableText,"double")){
            return "double";
        }
        if(presentableText.contains("Float") || StringUtils.equals(presentableText,"float")){
            return "float";
        }
        if(presentableText.contains("Date") || StringUtils.equals(presentableText,"Time")){
            return "date";
        }
        return "object";
    }

    private static String getRequestUrl(PsiModifierListOwner target, String fullNameAnnotation, Project project) {
        PsiAnnotation psiAnnotation= PsiAnnotationSearchUtil.findAnnotation(target, fullNameAnnotation);
        if(Objects.isNull(psiAnnotation)){
            return null;
        }
        PsiNameValuePair[] psiNameValuePairs= psiAnnotation.getParameterList().getAttributes();
        if(psiNameValuePairs.length>0){
            if(psiNameValuePairs[0].getLiteralValue()!=null) {
                return psiNameValuePairs[0].getLiteralValue();
            }else{
                PsiAnnotationMemberValue psiAnnotationMemberValue=psiAnnotation.findAttributeValue("value");
                if(psiAnnotationMemberValue.getReference()!=null){
                    PsiReference reference = psiAnnotationMemberValue.getReference();
                    PsiElement resolve = reference.resolve();
                    String text = resolve.getText();
                    String[] results= text.split("=");
                    return results[results.length - 1].split(";")[0].replace("\"", "").trim();
                }else{
                    return null;
                }
            }
        }
        return null;
    }

}
