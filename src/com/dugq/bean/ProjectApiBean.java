package com.dugq.bean;

import com.dugq.pojo.enums.RequestType;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

/**
 * @author dugq
 * @date 2021/8/15 11:46 下午
 */
public class ProjectApiBean {


    private PsiMethod psiElement;
    @Nullable
    private String path;

    private RequestType requestType;

    public PsiMethod getPsiElement() {
        return psiElement;
    }

    public void setPsiElement(PsiMethod psiElement) {
        this.psiElement = psiElement;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    public void setPath(@Nullable String path) {
        this.path = path;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
