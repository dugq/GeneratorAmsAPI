package com.dugq.util.psidocment;

import com.dugq.exception.ErrorException;
import com.dugq.pojo.ams.ParamSelectValue;
import com.dugq.util.MyPsiTypesUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述工具
 *
 * @author chengsheng@qbb6.com
 * @date 2019/4/30 4:13 PM
 */
public abstract class DocumentUtil {

    public static final Map<String,String> defaultPropsMap = new HashMap<>();

    static final String DATE_PARAMETER_FORMAT_ANNOTATION = "org.springframework.format.annotation.DateTimeFormat";

    static {
        defaultPropsMap.put("success","接口是否成功标志");
        defaultPropsMap.put("code","接口请求状态码");
        defaultPropsMap.put("pageIndex","分页-当前页");
        defaultPropsMap.put("pageSize","分页-每页行数");
        defaultPropsMap.put("desc","描述");
        defaultPropsMap.put("timestamp","时间戳");
        defaultPropsMap.put("data","数据集合");
    }

    public static Optional<String> getDefaultDesc(String propsName){
        return Optional.ofNullable(defaultPropsMap.get(propsName));
    }

    /**
     * 对文档注释中tag处理的模版方法。获取说是从注释中查找指定标签元素
     */
    static final BiFunction<PsiDocComment, Predicate<PsiDocTag>,PsiDocTag> FIND_TAG_FROM_PSI_DOC_TEMP = (psiDocComment, predicate) ->{
        if (Objects.isNull(psiDocComment)){
            return null;
        }
        final PsiDocTag[] tags = psiDocComment.getTags();
        if (ArrayUtils.isEmpty(tags)){
            return null;
        }
        for (PsiDocTag tag : tags) {
            if (predicate.test(tag)){
                return tag;
            }
        }
        return null;
    };


    /**
     * 获取tag注释的第一个非空行
     * @param tag 注释中的tag
     */
    public static String getFirstNotBlankLineOfDocTag(PsiDocTag tag) {
        if (Objects.isNull(tag)){
            return null;
        }
        final PsiElement[] dataElements = tag.getDataElements();
        if (ArrayUtils.isEmpty(dataElements)){
            return null;
        }
        for (PsiElement dataElement : dataElements) {
            final String text = formatLineDoc(dataElement.getText());
            if (StringUtils.isNotBlank(text)){
                return text.trim();
            }
        }
        return null;
    }



    static String subStringWithReg(String source,String reg){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(source);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

    //获取注释的第一行
    public static String getFirstNotBlankLineOfDocComment(PsiDocComment psiDocComment){
        if (Objects.isNull(psiDocComment)){
            return null;
        }
        //doc对象的每一行都是一个PsiElement对象
        final PsiElement[] children = psiDocComment.getChildren();
        if (ArrayUtils.isEmpty(children)){
            return null;
        }
        for (PsiElement child : children) {
            final String lineDoc = formatLineDoc(child.getText());
            if (StringUtils.isNotBlank(lineDoc)){
                return lineDoc.trim();
            }
        }
        return null;
    }

    //去掉空格，/ * 等javadoc的格式字符
    static String formatLineDoc(String lineText){
        if (StringUtils.isBlank(lineText)){
            return null;
        }
        return lineText.replace("show ","").replace("*", "").replace("/", "").replace(" ", "").replace("\n", "").replace("\t", "");
    }

    public static String getConstantAttributeFromAnnotation(PsiAnnotation annotation,String propsName){
        if (Objects.isNull(annotation)){
            return null;
        }
        final JvmAnnotationAttribute pattern = annotation.findAttribute(propsName);
        if (Objects.isNull(pattern)){
            return null;
        }
        final JvmAnnotationAttributeValue attributeValue = pattern.getAttributeValue();
        if (attributeValue instanceof JvmAnnotationConstantValue){
            final Object constantValue = ((JvmAnnotationConstantValue) attributeValue).getConstantValue();
            if (Objects.isNull(constantValue)){
                return null;
            }
            return constantValue.toString();
        }else{
            throw new ErrorException("只能解析常量注解属性");
        }
    }

    @Nullable
    public static List<ParamSelectValue> getSelectValueFromEnumPsiClass(Project project, String classFullName) {
        if(Objects.isNull(project) || StringUtils.isBlank(classFullName)){
            return null;
        }
        PsiClass psiClassChild = MyPsiTypesUtils.getPsiClassByName(classFullName,project);
        if(Objects.isNull(psiClassChild)){
            return Collections.emptyList();
        }
        if(!psiClassChild.isEnum()){
            return null;
        }
        PsiField[] allFields = psiClassChild.getAllFields();
        List<ParamSelectValue> list = new ArrayList<>();
        for (PsiField filed : allFields) {
            if(filed instanceof PsiEnumConstant){
                PsiExpressionList argumentList = ((PsiEnumConstant) filed).getArgumentList();
                if(Objects.isNull(argumentList)){
                    continue;
                }
                PsiExpression[] expressions = argumentList.getExpressions();
                if(expressions.length>1){
                    ParamSelectValue selectValue = new ParamSelectValue();
                    selectValue.setValue(expressions[0].getText());
                    selectValue.setValueDescription(expressions[expressions.length-1].getText());
                    list.add(selectValue);
                }
            }
        }
        return list;
    }

    /**
     * 从一组psi元素中，取出第一个以e.g: 开头的注释行，作为参数的示例值
     * @param dataElements 文档元素列表哦！支持：如果是方法的参数，请先读取到参数标签，如果是字段参数，直接给字段的块注释即可
     * @return 截取 e.g: 后面的字符串
     */
    public static String getVarExampleValue(PsiElement[] dataElements){
        for (PsiElement dataElement : dataElements) {
            if (StringUtils.isBlank(dataElement.getText())){
                continue;
            }
            final String text = dataElement.getText().trim();
            if (text.startsWith("e.g:")){
                return text.replace("e.g:","").trim();
            }
        }
        return null;
    }

}
