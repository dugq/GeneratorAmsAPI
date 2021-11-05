package com.dugq.exception;

import com.dugq.pojo.ThreadStack;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * Created by dugq on 2019/12/30.
 */
public class ErrorException extends RuntimeException {
    private static final long serialVersionUID = 3625164330290417233L;
    private PsiMethod method;
    private PsiField psiField;
    private String desc;

    public ErrorException(PsiMethod method, PsiField psiField,String desc) {
        this.method = method;
        this.psiField = psiField;
        this.desc = desc;
    }

    public ErrorException(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        if (Objects.isNull(method)){
            return desc;
        }
        return "生产接口发送错误：" + desc+
                "method=" + method +
                ", field=" + psiField ;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    public String getFullMessage(){
        final PsiMethod method = ThreadStack.getMethod();
        StringBuilder msg = new StringBuilder();
        if (Objects.nonNull(method)){
            msg.append("接口【"+method.getContainingClass().getName()+"#"+method.getName()+"】");
        }
        final String stack = ThreadStack.getStack();
        if (StringUtils.isNotBlank(stack)){
            msg.append("坐标【").append(stack).append("】");
        }
        msg.append("发生错误: ").append(getMessage());
        return msg.toString();
    }

}
