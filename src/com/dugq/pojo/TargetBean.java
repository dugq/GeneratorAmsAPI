package com.dugq.pojo;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

/**
 * Created by dugq on 2021/3/23.
 */
public class TargetBean {

    private PsiClass containingClass;
    private PsiMethod containingMethod;

    public TargetBean(PsiClass containingClass, PsiMethod containingMethod) {
        this.containingClass = containingClass;
        this.containingMethod = containingMethod;
    }

    public PsiClass getContainingClass() {
        return containingClass;
    }

    public PsiMethod getContainingMethod() {
        return containingMethod;
    }
}
