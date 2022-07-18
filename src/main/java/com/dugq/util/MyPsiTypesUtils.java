package com.dugq.util;

import com.dugq.exception.ErrorException;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.structuralsearch.Scopes;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NonNls;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 各种常用工具类
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/30 9:58 AM
 */
public class MyPsiTypesUtils  {

    @NonNls
    public static final Map<String, Object> normalTypes = new HashMap<>();

    public static final Map<String,Object> noramlTypesPackages=new HashMap<>();

    public static final Map<String,Object> collectTypes=new HashMap<>();

    public static final Map<String,Object> collectTypesPackages=new HashMap<>();
    /**
     * 泛型列表
     */
    public static final List<String> genericList=new ArrayList<>();
    /**
     * 日期类型
     */
    public static final List<String> dateList = new ArrayList<>();
    /**
     * 固定跳过的字段
     */
    public static final List<String> skipFiled = new ArrayList<>();
    /**
     * 跳过request等入参
     */
    public static final List<String> skipParams = new ArrayList<>();

    public static final List<String> NOT_NULL_ANNOTATIONS = new ArrayList<>();



    static {
        normalTypes.put("int",1);
        normalTypes.put("boolean",false);
        normalTypes.put("byte",1);
        normalTypes.put("short",1);
        normalTypes.put("long",1L);
        normalTypes.put("float",1.0F);
        normalTypes.put("double",1.0D);
        normalTypes.put("char",'a');
        normalTypes.put("Boolean", false);
        normalTypes.put("Byte", 0);
        normalTypes.put("Short", Short.valueOf((short) 0));
        normalTypes.put("Integer", 0);
        normalTypes.put("Long", 0L);
        normalTypes.put("Float", 0.0F);
        normalTypes.put("Double", 0.0D);
        normalTypes.put("String", "String");
        normalTypes.put("BigDecimal",0.111111);
        collectTypes.put("HashMap","HashMap");
        collectTypes.put("Map","Map");
        collectTypes.put("LinkedHashMap","LinkedHashMap");

        genericList.add("T");
        genericList.add("E");
        genericList.add("A");
        genericList.add("B");
        genericList.add("K");
        genericList.add("V");

        dateList.add("LocalDate");
        dateList.add("LocalTime");
        dateList.add("LocalDateTime");
        dateList.add("Timestamp");
        dateList.add("Date");

        skipFiled.add("serialVersionUID");
        skipFiled.add("offset");

        skipParams.add("HttpServletRequest");
        skipParams.add("Model");
        skipParams.add("HttpServletResponse");
        skipParams.add("ModelAndView");

        skipParams.add("KjjHttpRequest");
        skipParams.add("KjjHttpResponse");

        NOT_NULL_ANNOTATIONS.add("javax.validation.constraints.NotNull");
        NOT_NULL_ANNOTATIONS.add("org.hibernate.validator.constraints.NotNull");
        NOT_NULL_ANNOTATIONS.add("javax.validation.constraints.NotBlank");
        NOT_NULL_ANNOTATIONS.add("org.hibernate.validator.constraints.NotBlank");
        NOT_NULL_ANNOTATIONS.add("javax.validation.constraints.NotEmpty");
    }

    static {
        noramlTypesPackages.put("int",1);
        noramlTypesPackages.put("boolean",true);
        noramlTypesPackages.put("byte",1);
        noramlTypesPackages.put("short",1);
        noramlTypesPackages.put("long",1L);
        noramlTypesPackages.put("float",1.0F);
        noramlTypesPackages.put("double",1.0D);
        noramlTypesPackages.put("char",'a');
        noramlTypesPackages.put("java.lang.Boolean",false);
        noramlTypesPackages.put("java.lang.Byte",0);
        noramlTypesPackages.put("java.lang.Short",Short.valueOf((short) 0));
        noramlTypesPackages.put("java.lang.Integer",1);
        noramlTypesPackages.put("java.lang.Long",1L);
        noramlTypesPackages.put("java.lang.Float",1L);
        noramlTypesPackages.put("java.lang.Double",1.0D);
        noramlTypesPackages.put("java.sql.Timestamp",new Timestamp(System.currentTimeMillis()));
        noramlTypesPackages.put("java.util.Date", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
        noramlTypesPackages.put("java.lang.String","String");
        noramlTypesPackages.put("java.math.BigDecimal",1);
        noramlTypesPackages.put("java.time.LocalDate", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        noramlTypesPackages.put("java.time.LocalTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        noramlTypesPackages.put("java.time.LocalDateTime", new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));

        collectTypesPackages.put("java.util.LinkedHashMap","LinkedHashMap");
        collectTypesPackages.put("java.util.HashMap","HashMap");
        collectTypesPackages.put("java.util.Map","Map");
    }

    /**
     * Spring MVC提供的额外对象，不需要解析
     */
    public static boolean isSkipType(PsiType psiType){
        return  skipParams.contains(psiType.getPresentableText());
    }

    public static boolean isSkipFiled(String filedName){
        return skipFiled.contains(filedName);
    }

    public static boolean isParameterDeprecated(PsiParameter psiParameter){
        return psiParameter.hasAnnotation("java.lang.Deprecated") || (!isPrimitiveType(psiParameter.getType()) && getClassByType(psiParameter.getType()).hasAnnotation("java.lang.Deprecated"));
    }

    public static boolean isFiledDeprecated(PsiField psiField){
        return psiField.hasAnnotation("java.lang.Deprecated") || (!isPrimitiveType(psiField.getType()) && getClassByType(psiField.getType()).hasAnnotation("java.lang.Deprecated"));
    }

    /**
     * java primitive, String,等直接变量都被认为是基本类型
     */
    public static boolean isPrimitiveType(PsiType psiType) {
        String presentableText = psiType.getPresentableText();
        return psiType instanceof PsiPrimitiveType || normalTypes.containsKey(presentableText);
    }

    public static boolean isPrimitiveOrDateType(PsiType psiType){
        return isPrimitiveType(psiType) || isDateType(psiType);
    }

    /**
     * mock type
     * @param type
     * @return
     */
    public static JsonObject formatMockType(String type) {
        JsonObject mock = new JsonObject();
        if (type.equals("int")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("boolean")){
            mock.addProperty("mock", "@boolean");
        }else if (type.equals("byte")){
            mock.addProperty("mock", "@byte");
        }else if (type.equals("short")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("long")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("float")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("double")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("char")){
            mock.addProperty("mock", "@char");
        }else if (type.equals("Boolean")){
            mock.addProperty("mock", "@boolean");
        }else if (type.equals("Byte")){
            mock.addProperty("mock", "@byte");
        }else if (type.equals("Short")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("Integer")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("Long")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("Float")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("Double")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("String")){
            mock.addProperty("mock", "@string");
        }else if (type.equals("Date")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("BigDecimal")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("LocalDate")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("LocalTime")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("LocalDateTime")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("Timestamp")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.lang.Boolean")){
            mock.addProperty("mock", "@boolean");
        }else if (type.equals("java.lang.Byte")){
            mock.addProperty("mock", "@byte");
        }else if (type.equals("java.lang.Short")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("java.lang.Integer")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("java.lang.Long")){
            mock.addProperty("mock", "@integer");
        }else if (type.equals("java.lang.Float")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("java.lang.Double")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("java.sql.Timestamp")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.util.Date")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.lang.String")){
            mock.addProperty("mock", "@string");
        }else if (type.equals("java.math.BigDecimal")){
            mock.addProperty("mock", "@float");
        }else if (type.equals("java.time.LocalDate")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.time.LocalTime")){
            mock.addProperty("mock", "@timestamp");
        }else if (type.equals("java.time.LocalDateTime")){
            mock.addProperty("mock", "@timestamp");
        }else{
            mock.addProperty("mock", "mock");
        }
        return mock;
    }

    public static boolean isListType(PsiType psiType, Project project){
       return getPsiTypeByName("java.util.List", project).isAssignableFrom(psiType);
    }

    /**
     * 创建一个psiTYpe。目前发现，已有类通过此方法和 {@link #getPsiTypeByName }方法没啥区别，具体实现也看不懂。留作以后研究。
     */
    public static PsiType createPsiTypeByName(String name,Project project){
        return JavaPsiFacade.getInstance(project).getElementFactory().createTypeFromText(name, null);
    }

    public static boolean isSetType(PsiType psiType, Project project){
        return getPsiTypeByName("java.util.Set", project).isAssignableFrom(psiType);
    }

    public static boolean isMapType(PsiType psiType, Project project){
        return getPsiTypeByName("java.util.Map", project).isAssignableFrom(psiType);
    }

    public static boolean isCollectionType(PsiType psiType, Project project){
        return isSetType(psiType,project) || isListType(psiType,project);
    }

    public static boolean isArray(PsiType psiType){
        return psiType instanceof PsiArrayType;
    }

    //获取数组中元素的类型
    public static PsiType getArrayType(PsiType psiType){
        if (!isArray(psiType)){
            return null;
        }
        PsiArrayType arrayType = (PsiArrayType) psiType;
        return arrayType.getDeepComponentType();
    }

    public static boolean isDateType(PsiType psiType){
        return dateList.contains(psiType.getPresentableText());
    }

    /**
     * 获取变量定义时的范型声明table
     * eg: Map<String,String>
     *     return
     *     K : java.lang.String
     *     V : java.lang.String
     */
    public static Map<String,PsiType> getPsiTypeGenericTypes(PsiType psiType){
        if (isPrimitiveType(psiType)){
            return new HashMap<>();
        }
        //1、获取到class 声明的范型列表
        final PsiClass psiClass = getClassByType(psiType);
        final List<String> genericNameList = getGenericNameList(psiClass);
        if (CollectionUtils.isEmpty(genericNameList)){
            return new HashMap<>();
        }

        //2、解析变量声明的泛型列表
        final PsiClassType psiClassType = (PsiClassType)GenericsUtil.getVariableTypeByExpressionType(psiType);
        final PsiType[] parameters = psiClassType.getParameters();
        if (genericNameList.size()!=parameters.length){
            throw new ErrorException("变量声明："+psiType.getCanonicalText()+" 不符合类限定名："+psiClass.getName()+" 导致类型推倒失败！");
        }
        final HashMap<String, PsiType> result = new HashMap<>();
        for (int i = 0, genericNameListSize = genericNameList.size(); i < genericNameListSize; i++) {
            String s = genericNameList.get(i);
                result.put(s, parameters[i]);
        }
        return result;
    }

    public static PsiType getCollectionGenericTypes(PsiType psiType,Project project,String paramFullName){
        final Map<String, PsiType> psiTypeGenericTypes = MyPsiTypesUtils.getPsiTypeGenericTypes(psiType);
        if (MapUtils.isEmpty(psiTypeGenericTypes)){
            throw new ErrorException(null,null,psiType.getCanonicalText()+" "+paramFullName+"范型未声明");
        }
        //为什么是E呢，因为Collection的范型声明为E。
        return psiTypeGenericTypes.get("E");
    }

    /**
     * 获取类声明的范型table
     * eg: Class A extends AbstractList<B>
     *
     *     return
     *       E : B
     * 注：接口暂不支持，理论来说接口也不需要范型解析
     */
    public static Map<String,PsiType> getPsiClassGenericTypes(PsiClass psiClass){
        final PsiClassType[] extendsListTypes = psiClass.getExtendsListTypes();
        if (ArrayUtils.isEmpty(extendsListTypes)){
            return new HashMap<>();
        }
        Map<String,PsiType> result = new HashMap<>();
        for (PsiClassType psiClassType : extendsListTypes) {
            result.putAll(getPsiTypeGenericTypes(psiClassType));
        }
        return result;
    }

    /**
     * 获取Class定义时声明的范型列表。
     * eg: java.util.Map<K,V>
     *   return : [K,V]
     */
    public static List<String> getGenericNameList(PsiClass psiClass){
        final PsiTypeParameter[] typeParameters = psiClass.getTypeParameters();
        if (ArrayUtils.isEmpty(typeParameters)){
            return new ArrayList<>();
        }
       return Stream.of(typeParameters).map(PsiTypeParameter::getName).collect(Collectors.toList());
    }

    //通过类名的完整限定名获取psi类型对象
    public static PsiType getPsiTypeByName(String className,Project project){
        final PsiClass psiClass = getPsiClassByName(className, project);
        return getTypeByClass(psiClass);
    }

    //通过类名的完整限定名获取psi对象
    public static PsiClass getPsiClassByName(String className,Project project){
        final PsiManager manager = PsiManager.getInstance(project);
        return ClassUtil.findPsiClass(manager, className);
    }

    //type 转 class
    public static PsiType getTypeByClass(PsiClass psiClass){
        return PsiTypesUtil.getClassType(psiClass);
    }

    //class 转 type
    public static PsiClass getClassByType(PsiType psiType){
        return PsiTypesUtil.getPsiClass(psiType);
    }

    /**
     * 获取类型简写
     */
    public static String getPresentType(PsiType childTypes) {
        if (isDateType(childTypes)){
            return "date";
        }
        return childTypes.getPresentableText().toLowerCase();
    }

    //获取默认值
    public static Object getDefaultValue(PsiType psiType) {
        if (isDateType(psiType)){
            return new Date().getTime();
        }
        if (StringUtils.equals(psiType.getPresentableText(), "String")){
            return "string";
        }
        if (StringUtils.equals(psiType.getPresentableText(), "Boolean") || StringUtils.equals(psiType.getPresentableText(), "boolean")){
            return true;
        }
        if (StringUtils.equals(psiType.getPresentableText(), "Character") || StringUtils.equals(psiType.getPresentableText(), "char")){
            return 'a';
        }
        return 1;
    }

    //判断变量声明是否含有非空注解
    public static boolean isParameterAnnotatedNotNull(PsiParameter psiParameter){
        return isJvmModifiersOwnerNotNull(psiParameter);
    }

    //判断字段是否含有非空注解
    public static boolean isFiledAnnotatedNotNull(PsiField psiField){
        return isJvmModifiersOwnerNotNull(psiField);
    }

    //判断对象是否含有非空注解
    public static boolean isJvmModifiersOwnerNotNull(PsiJvmModifiersOwner owner){
        final PsiAnnotation[] annotations = owner.getAnnotations();
        final List<String> annotationNames = Stream.of(annotations).map(PsiAnnotation::getQualifiedName).collect(Collectors.toList());
        return CollectionUtils.containsAny(annotationNames,NOT_NULL_ANNOTATIONS);
    }

    public static boolean hasSupperClass(PsiClass psiClass){
        final PsiClass superClass = psiClass.getSuperClass();
        if (Objects.isNull(superClass) || StringUtils.equals("java.lang.Object",superClass.getQualifiedName())){
            return false;
        }
        return true;
    }

    public static boolean isElementInLiberay(PsiElement element){
        return Scopes.getType(element.getResolveScope())== Scopes.Type.NAMED;
    }

    public static boolean isVoid(PsiType psiType) {
        return psiType.getCanonicalText().equals("java.lang.Void") || psiType.getCanonicalText().equals("void");
    }
}
