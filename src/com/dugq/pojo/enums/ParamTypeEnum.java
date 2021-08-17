package com.dugq.pojo.enums;

import com.dugq.util.MyPsiTypesUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationClassValue;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJvmModifiersOwner;
import com.intellij.psi.PsiType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author dugq
 * @date 2021/7/5 2:43 下午
 */
public enum ParamTypeEnum {

    STRING(0,"String"),
    FILE(1,"file"),
    JSON(2,"JSON"),
    INT(3,"int"),
    FLOAT(4,"float"),
    DOUBLE(5,"double"),
    TIME(6,"Time"),
    DATE_TIME(7,"DateTime"),
    BOOLEAN(8,"boolean"),
    BYTE(9,"byte"),
    SHORT(10,"short"),
    LONG(11,"long"),
    ARRAY(12,"array"),
    OBJECT(13,"object"),
    NUMBER(14,"number"),
    UNKNOWN(15,"未知类型");

    private final int type;
    private final String name;

    private static final List<ParamTypeEnum> normalType = new ArrayList<>();

    static {
        normalType.add(STRING);
        normalType.add(INT);
        normalType.add(FLOAT);
        normalType.add(DOUBLE);
        normalType.add(TIME);
        normalType.add(DATE_TIME);
        normalType.add(BOOLEAN);
        normalType.add(BYTE);
        normalType.add(SHORT);
        normalType.add(LONG);
        normalType.add(NUMBER);
    }

    ParamTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }


    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static ParamTypeEnum getTypeByPsiType(PsiType psiType, PsiJvmModifiersOwner psiJvmModifiersOwner, Project project){
        String presentableText = psiType.getPresentableText();
        if(StringUtils.equals(presentableText,"String")){
            return STRING;
        }
        if(presentableText.contains("JSON")){
            return JSON;
        }
        if(presentableText.contains("Integer") || StringUtils.equals(presentableText,"int")){
            return INT;
        }
        if(presentableText.contains("Float") || StringUtils.equals(presentableText,"float")){
            return FLOAT;
        }
        if(presentableText.contains("Double") || StringUtils.equals(presentableText,"double")){
            return DOUBLE;
        }
        if(presentableText.contains("Date") || StringUtils.equals(presentableText,"Time")){
            return DATE_TIME;
        }
        if(presentableText.contains("Boolean") || StringUtils.equals(presentableText,"boolean")){
            return BOOLEAN;
        }
        if(presentableText.contains("Byte") || StringUtils.equals(presentableText,"byte")){
            return BYTE;
        }
        if(presentableText.contains("Short") || StringUtils.equals(presentableText,"'short'")){
            return SHORT;
        }
        if(presentableText.contains("Long") || StringUtils.equals(presentableText,"long")){
            if (isEncodeId(psiJvmModifiersOwner)){
                return STRING;
            }
            return LONG;
        }
        if(MyPsiTypesUtils.isCollectionType(psiType,project) || MyPsiTypesUtils.isArray(psiType)){
            return ARRAY;
        }
        return OBJECT;
    }


    private static boolean isEncodeId(PsiJvmModifiersOwner psiJvmModifiersOwner){
        if (Objects.isNull(psiJvmModifiersOwner)){
            return false;
        }
        return Arrays.stream(psiJvmModifiersOwner.getAnnotations()).anyMatch((ParamTypeEnum::isAnnotatedEncode));
    }

    private static boolean isAnnotatedEncode(PsiAnnotation annotation) {
        if (Objects.isNull(annotation)){
            return false;
        }
        final String annotationQualifiedName = annotation.getQualifiedName();

        if (StringUtils.isBlank(annotationQualifiedName)){
            return false;
        }
        //@IdDecode 注解的字段
        if (annotationQualifiedName.contains("IdDecode")){
            return true;
        }

        //fastJSON序列化的字段
        if (isAnotatedIdEncode(annotation, annotationQualifiedName,"com.alibaba.fastjson.annotation.JSONField","serializeUsing")){
            return true;
        }
        //jackson序列化的字段
        return isAnotatedIdEncode(annotation, annotationQualifiedName,"com.fasterxml.jackson.databind.annotation.JsonSerialize","using");
    }

    private static boolean isAnotatedIdEncode(PsiAnnotation annotation, String annotationQualifiedName,String sourceAnnotation,String attrName) {
        if (!StringUtils.equals(sourceAnnotation, annotationQualifiedName)) {
            return false;
        }
        final JvmAnnotationAttribute serializeUsing = annotation.findAttribute(attrName);
        if (Objects.isNull(serializeUsing)) {
            return false;
        }
        final JvmAnnotationAttributeValue attributeValue = serializeUsing.getAttributeValue();
        if (Objects.isNull(attributeValue)) {
            return false;
        }
        if (!(attributeValue instanceof JvmAnnotationClassValue)) {
            return false;
        }
        final String className = ((JvmAnnotationClassValue) attributeValue).getQualifiedName();
        if (StringUtils.isBlank(className)) {
            return false;
        }
        final PsiClass usingClass = MyPsiTypesUtils.getPsiClassByName(className, annotation.getProject());
        if (Objects.isNull(usingClass)) {
            return false;
        }
        final String name = usingClass.getName();
        return idSerializableList.contains(name);
    }

    private static List<String> idSerializableList = new ArrayList<>();
    static {
        idSerializableList.add("IdFastJsonSerializable");
        idSerializableList.add("IdBase64Serializable");
        idSerializableList.add("IdListBase64Serializable");
        idSerializableList.add("IdNumberSerializable");
    }

    public static String[] getAllTypeNames(){
        return Stream.of(values()).map(ParamTypeEnum::getName).toArray((String[]::new));
    }

    public static ParamTypeEnum getByName(String name){
        for (ParamTypeEnum enums : values()) {
            if (StringUtils.equals(enums.getName(),name)){
                return enums;
            }
        }
        return null;
    }

    public static ParamTypeEnum getByCode(Integer valueType) {
        for (ParamTypeEnum enums : values()) {
            if (Objects.equals(enums.getType(),valueType)){
                return enums;
            }
        }
        return null;
    }


    public boolean isNormalType(){
        return normalType.contains(this);
    }
}
