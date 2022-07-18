package com.dugq.util.psidocment;

import com.dugq.pojo.ams.ParamSelectValue;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJvmModifiersOwner;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author dugq
 * @date 2021/7/1 5:11 下午
 * 方法的参数，和class 的属性基本一值，所以代码兼容。
 */
public class ParameterUtils extends DocumentUtil{


    /**
     * 从注释块中查找enum
     */
    public static List<ParamSelectValue> getSelectValuesFromDoc(PsiDocComment psiDocComment, String varName){
        if(Objects.isNull(psiDocComment) || StringUtils.isBlank(varName)){
            return null;
        }
        //获取注释文档注释的对象
        final PsiElement context = psiDocComment.getContext();
        //方法注释
        if (context instanceof PsiMethod){
             String className = FIND_TAG_FROM_PSI_DOC_TEMP
                    .andThen(DocumentUtil::getFirstNotBlankLineOfDocTag)
                    .apply(psiDocComment, isVarParamTag(varName));
            return getSelectValueFromEnumPsiClass(psiDocComment.getProject(),className);
            //字段注释
        }else if (context instanceof PsiField){
            if (Arrays.stream(psiDocComment.getTags()).anyMatch(tag->tag.getName().equals("see"))){
                final PsiDocTag see = Arrays.stream(psiDocComment.getTags()).filter(tag -> tag.getName().equals("see")).findFirst().orElse(null);
                return getSelectValueFromEnumPsiClass(psiDocComment.getProject(),see.getDataElements()[0].getText());
            }
            String className = getLinkOrSeeClassInDoc(psiDocComment.getDescriptionElements());
            return getSelectValueFromEnumPsiClass(psiDocComment.getProject(),className);
        }else{
            return null;
        }

    }

    /**
     * 判断是否是指定参数的注解标签
     */
    private static Predicate<PsiDocTag> isVarParamTag(String varName){
        return (tag)-> Objects.equals(tag.getName(), "param")
                && Objects.nonNull(tag.getValueElement())
                && StringUtils.equals(tag.getValueElement().getText(),varName);
    }

    /**
     * 从一堆块注释中读取@see @link @linkplain 注释中
     * @param dataElements 文档注释元素列表哦！支持：如果是方法的参数，请先读取到参数标签，如果是字段参数，直接给字段的块注释即可
     */
    private static String getLinkOrSeeClassInDoc(PsiElement[] dataElements) {
        for (PsiElement dataElement : dataElements) {
            String elementContent = dataElement.getText();
            if (StringUtils.isBlank(elementContent)){
                continue;
            }
            if(elementContent.contains("@link")){
                elementContent = subStringWithReg(elementContent, "@link.*?}").trim();
                return elementContent.replace("@link","").replace("}","").trim();
            } else if(elementContent.contains("@see")){
                elementContent = subStringWithReg(elementContent, "@see.*?}").trim();
                return elementContent.replace("@see","").replace("}","").trim();
            }else if(elementContent.contains("@linkplain")){
                elementContent = subStringWithReg(elementContent, "@linkplain.*?}").trim();
                return elementContent.replace("@linkplain","").replace("}","").trim();
            }
        }
        return null;
    }


    /**
     * 从注释文档中读取变量的说明
     * @param varName 变量名称
     * @param psiDocComment 注释文档对象
     * @return
     */
    public static String getVarSimpleDescFromDocComment(String varName, PsiDocComment psiDocComment){
        if(StringUtils.isBlank(varName)){
            return null;
        }
        if (Objects.isNull(psiDocComment)){
            return getDefaultDesc(varName).orElse(null);
        }
        //获取注释文档注释的对象
        final PsiElement context = psiDocComment.getContext();
        if (context instanceof PsiMethod){
            return FIND_TAG_FROM_PSI_DOC_TEMP
                    .andThen(ParameterUtils::getParamSimpleDescFromTag)
                    .apply(psiDocComment, isVarParamTag(varName));
        }else if (context instanceof PsiField){
            return getFirstNotBlankLineOfDocComment(psiDocComment);
        }else{
            return null;
        }
    }

    /**
     * 获取参数描述的第一段为参数的简单描述
     * @param tag 参数描述标签
     * @return
     */
    private static String getParamSimpleDescFromTag(PsiDocTag tag) {
        //取标签的数据列表的第2段为变量的描述。 数据列表 第一段一定为标签的value，即字段名称，后面纯文字以回车符分割段，或者其他javadoc的tag分段
        //eg: @param liveId 简单描述 \r\n 细节描述 {@link XXXX} 它的分段为：
        //["liveId","简单描述 ","细节描述 ","{@link XXXX}","XXXX"]
        //我们取固定的"简单描述"为字段描述
        if (Objects.isNull(tag)){
            return null;
        }
        final PsiElement[] dataElements = tag.getDataElements();
        if (ArrayUtils.isEmpty(dataElements)){
            return null;
        }
        if (dataElements.length<=1){
            return null;
        }
        return dataElements[1].getText();
    }


    /**
     * 获取变量的示例值
     * 1、方法参数变量，从方法的块注释中获取    格式：参数注释单行  @param liveId 直播ID
     *                                                                  e.g: value
     * 2、class属性变量，从属性的块注释中获取  格式：块注释中的单行 e.g: value
     * @param varName 变量名称
     * @param psiDocComment 注释块
     */
    public static String getVarExampleValue(String varName,PsiDocComment psiDocComment){
        if(Objects.isNull(psiDocComment) || StringUtils.isBlank(varName)){
            return null;
        }
        //获取注释文档注释的对象
        final PsiElement context = psiDocComment.getContext();
        //方法注释
        if (context instanceof PsiMethod){
            /*
             * tag的数据列表格式为： 第一段一定为tag的value，即字段名称，后面纯文字以回车符分割段，或者其他javadoc的tag分段
             * eg: @param liveId 简单描述 \r\n e.g: 123 \r\n 细节描述 {@link XXXX}
             * 它的分段为：
             * ["liveId","简单描述 ","e.g: 123 ","细节描述 ","{@link XXXX}","XXXX"]
             * 我们遍历分段，获取第一个 以e.g: 开头的描述行作为参数的示例值
             *
             */
            return FIND_TAG_FROM_PSI_DOC_TEMP
                    .andThen((tag)->Objects.isNull(tag)?null:ParameterUtils.getVarExampleValue(tag.getDataElements()))
                    .apply(psiDocComment, isVarParamTag(varName));
            //字段注释
        }else if (context instanceof PsiField){
            return getVarExampleValue(psiDocComment.getDescriptionElements());
        }else{
            return null;
        }
    }

    /**
     * 读取spring @DateTimeFormat 注解的格式配置
     * @param psiOwner 参数列表
     */
    public static String getDateFormat(PsiJvmModifiersOwner psiOwner) {
        final PsiAnnotation annotation = psiOwner.getAnnotation(DATE_PARAMETER_FORMAT_ANNOTATION);
        return getConstantAttributeFromAnnotation(annotation,"pattern");
    }

}
