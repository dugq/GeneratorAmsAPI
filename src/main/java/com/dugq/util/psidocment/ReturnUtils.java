package com.dugq.util.psidocment;

import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * @author dugq
 * @date 2021/7/1 5:13 下午
 */
public class ReturnUtils extends DocumentUtil{

    /**
     * 读取方法返回值的简单描述
     * @param psiDocComment 方法的注释
     */
    public static String getMethodReturnSimpleDesc(PsiDocComment psiDocComment){
        return FIND_TAG_FROM_PSI_DOC_TEMP
                .andThen(DocumentUtil::getFirstNotBlankLineOfDocTag)
                .apply(psiDocComment,ReturnUtils::isReturnTag);
    }

    private static boolean isReturnTag(PsiDocTag psiDocTag){
        return StringUtils.equals(psiDocTag.getName(),"return");
    }



    /**
     * 获取返回值的示例值
     * @param psiDocComment 注释块
     */
    public static String getVarExampleValue(PsiDocComment psiDocComment){
        if (Objects.isNull(psiDocComment)){
            return null;
        }
        return FIND_TAG_FROM_PSI_DOC_TEMP
                .andThen((tag)-> {
                    if (Objects.isNull(tag)){
                        return null;
                    }
                    return Objects.isNull(tag)?null:ParameterUtils.getVarExampleValue(tag.getDataElements());

                })
                .apply(psiDocComment, ReturnUtils::isReturnTag);
    }
}
