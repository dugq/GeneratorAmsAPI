package com.dugq.util;

import com.dugq.pojo.enums.RequestType;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJvmModifiersOwner;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author dugq
 * @date 2021/8/16 10:45 上午
 */
public class RestfulHelper {

    public static String formatUrl(String parentUrl,String subUrl){
        if (StringUtils.isNotBlank(parentUrl)){
            parentUrl = addPreSeparatorAndRmLastSeparator(parentUrl);
        }
        if (StringUtils.isNotBlank(subUrl)){
            subUrl = addPreSeparatorAndRmLastSeparator(subUrl);
        }
        return parentUrl+subUrl;
    }

    public static String addPreSeparatorAndRmLastSeparator(String url){
        if (StringUtils.isBlank(url)){
            return url;
        }
        if (!url.startsWith("/")){
            url = "/"+url;
        }
        if (url.endsWith("/")){
            url = url.substring(0,url.length()-1);
        }
        return url;
    }

    /**
     *
     * @param psiMethod 带有注解的handler
     * @return url
     */
    public static String getRequestUrl(PsiMethod psiMethod){
        final PsiAnnotation requestMapping = getRequestAnnotation(psiMethod);
        if (Objects.isNull(requestMapping)){
            return null;
        }
        final PsiClass psiClass = psiMethod.getContainingClass();
        if (Objects.isNull(psiClass)){
            return null;
        }

        final String childPath = getRequestPath(requestMapping);
        final PsiAnnotation parentAnnotation = getRequestAnnotation(psiClass);
        if (Objects.nonNull(parentAnnotation)){
            final String parentPath = getRequestPath(parentAnnotation);
            return parentPath+childPath;
        }
        return childPath;
    }

    /**
     * 获取element的注解
     * @param psiElement PsiMethod || PsiClass
     * @return restful注解
     */
    public static PsiAnnotation getRequestAnnotation(@NotNull PsiJvmModifiersOwner psiElement) {
        final PsiAnnotation requestMapping = psiElement.getAnnotation(SpringMVCConstant.RequestMapping);
        if (Objects.nonNull(requestMapping)){
            return requestMapping;
        }
        final PsiAnnotation getMapping = psiElement.getAnnotation(SpringMVCConstant.GetMapping);
        if (Objects.nonNull(getMapping)){
            return getMapping;
        }
        return psiElement.getAnnotation(SpringMVCConstant.PostMapping);
    }

    /**
     * 从restful注解中获取mapping
     * @param psiAnnotation restful注解
     * @return 请求类型
     */
    public static RequestType getRequestType(PsiAnnotation psiAnnotation){
        if (StringUtils.equals(psiAnnotation.getQualifiedName(),SpringMVCConstant.GetMapping)){
            return RequestType.get;
        }
        if (StringUtils.equals(psiAnnotation.getQualifiedName(),SpringMVCConstant.PostMapping)){
            return RequestType.post;
        }
        if (StringUtils.equals(psiAnnotation.getQualifiedName(),SpringMVCConstant.RequestMapping)){
            final PsiNameValuePair method = AnnotationUtil.findDeclaredAttribute(psiAnnotation, "method");
            if (Objects.isNull(method)){
                return null;
            }
            final String text = method.getValue().getText();
        }

        return null;
    }

    /**
     * 从restful注解中获取mapping
     * @param psiAnnotation restful注解
     * @return 请求类型
     */
    public static String getRequestPath(PsiAnnotation psiAnnotation){
        final PsiNameValuePair method = AnnotationUtil.findDeclaredAttribute(psiAnnotation, "value");
        if (Objects.isNull(method)){
            return null;
        }

        final PsiAnnotationMemberValue value = method.getValue();
        if (value instanceof PsiArrayInitializerMemberValue){

        }
        return "";
    }

}
