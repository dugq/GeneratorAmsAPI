package com.dugq.util.psidocment;

import com.dugq.exception.ErrorException;
import com.dugq.pojo.enums.RequestType;
import com.dugq.util.SpringMVCConstant;
import com.dugq.util.RestfulHelper;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * @author dugq
 * @date 2021/7/1 5:11 下午
 * 字段、描述工具类
 */
public class MyMethodUtils extends DocumentUtil{

    /**
     * 从方法的注释中读取第一行作为方法的简单描述
     */
    public static String getMethodSimpleDesc(PsiMethod psiMethod){
        PsiDocComment docComment = psiMethod.getDocComment();
        if (Objects.isNull(docComment)){
            throw new ErrorException(psiMethod,null,"接口的注释呢！！");
        }
        String methodDesc = DocumentUtil.getFirstNotBlankLineOfDocComment(docComment);
        if(StringUtils.isBlank(methodDesc)){
            throw new ErrorException(psiMethod,null,"在方法注释中第一行声明接口的名称。换行写方法的注释");
        }
        return methodDesc;
    }

    public static RequestType getRequestType(PsiMethod psiMethod){
        if (psiMethod.hasAnnotation(SpringMVCConstant.GetMapping)){
            return RequestType.get;
        }
        if (psiMethod.hasAnnotation(SpringMVCConstant.PostMapping)){
            return RequestType.post;
        }
        throw new ErrorException(psiMethod,null,"请使用@GetMapping或者@PostMapping定义接口路径");
    }

    public static String getRequestMappings(PsiMethod containingMethod, PsiClass containingClass) {
        String mapping = getRequestMappingValue(containingClass.getAnnotation(SpringMVCConstant.RequestMapping));
        if(Objects.isNull(mapping)){
            mapping="";
        }
        String subMapping =  getRequestMappingValue(containingMethod.getAnnotation(SpringMVCConstant.GetMapping));
        if (StringUtils.isBlank(subMapping)){
            subMapping = getRequestMappingValue(containingMethod.getAnnotation(SpringMVCConstant.PostMapping));
        }
        return RestfulHelper.formatUrl(mapping,subMapping);
    }

    private static String getRequestMappingValue(PsiAnnotation annotation) {
        if (Objects.isNull(annotation)){
            return null;
        }
        final JvmAnnotationAttribute value = annotation.findAttribute("value");
        if (Objects.isNull(value)){
            return null;
        }
        final JvmAnnotationAttributeValue attributeValue = value.getAttributeValue();
        if (attributeValue instanceof JvmAnnotationConstantValue){
            final Object constantValue = ((JvmAnnotationConstantValue) attributeValue).getConstantValue();
            if (Objects.isNull(constantValue)){
                return null;
            }
            return constantValue.toString();
        }else{
            throw new ErrorException("@GetMapping和@PostMapping的value只能填写字符串");
        }
    }

}
