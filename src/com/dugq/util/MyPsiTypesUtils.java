package com.dugq.util;

import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本类
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/30 9:58 AM
 */
public class MyPsiTypesUtils {

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

    /**
     *  java primitive, String, 等直接变量都被认为是基本类型
     */
    public static boolean isNormalType(String typeName) {
        return normalTypes.containsKey(typeName);
    }

    /**
     * java primitive, String, date, 等直接变量都被认为是基本类型
     */
    public static boolean isPrimitiveType(PsiType psiType) {
        String presentableText = psiType.getPresentableText();
        return normalTypes.containsKey(presentableText) || isDate(psiType);
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

    public static boolean isList(PsiType psiType, Project project){
       return JavaPsiFacade.getInstance(project).getElementFactory().createTypeFromText("java.util.List", null).isAssignableFrom(psiType);
    }

    public static boolean isSet(PsiType psiType, Project project){
        return JavaPsiFacade.getInstance(project).getElementFactory().createTypeFromText("java.util.Set", null).isAssignableFrom(psiType);
    }

    public static boolean isMap(PsiType psiType, Project project){
        return JavaPsiFacade.getInstance(project).getElementFactory().createTypeFromText("java.util.Map", null).isAssignableFrom(psiType);
    }

    public static boolean isCollection(PsiType psiType, Project project){
        return isSet(psiType,project) || isList(psiType,project);
    }

    public static boolean isArray(PsiType psiType){
        return psiType instanceof PsiArrayType;
    }

    public static PsiType getArrayType(PsiType psiType){
        if (!isArray(psiType)){
            return null;
        }
        PsiArrayType arrayType = (PsiArrayType) psiType;
        return arrayType.getDeepComponentType();
    }

    public static boolean isDate(PsiType psiType){
        return dateList.contains(psiType.getPresentableText());
    }

    public static PsiType getChildTypes(PsiType psiType,Project project){
        if (isArray(psiType)){
            return psiType.getDeepComponentType();
        }else if(isCollection(psiType,project)){
            return PsiUtil.extractIterableTypeParameter(psiType,false);
        }
        return null;
    }

    /**
     * 获取类型简写
     */
    public static String getPresentType(PsiType childTypes) {
        if (isDate(childTypes)){
            return "date";
        }
        return childTypes.getPresentableText().toLowerCase();
    }

    public static PsiClass getClassByType(PsiType psiType,Project project){
        return JavaPsiFacade.getInstance(project).findClass(psiType.getCanonicalText(),GlobalSearchScope.allScope(project));
    }

    public static Object getDefaultValue(PsiType psiType) {
        if (isDate(psiType)){
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

}
