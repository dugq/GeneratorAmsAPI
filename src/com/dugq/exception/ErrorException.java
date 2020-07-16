package com.dugq.exception;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

/**
 * Created by dugq on 2019/12/30.
 */
public class ErrorException extends RuntimeException {
    private PsiMethod method;
    private PsiField psiField;
    private String desc;

    public ErrorException(PsiMethod method, PsiField psiField,String desc) {
        this.method = method;
        this.psiField = psiField;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "生产接口发送错误：" + desc+
                "method=" + method +
                ", field=" + psiField ;
    }
}
