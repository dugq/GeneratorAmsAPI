/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: SpringHelper
  Author:   ZhangYuanSheng
  Date:     2020/5/28 21:08
  Description:
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package com.dugq.util;

import com.dugq.bean.ProjectApiBean;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJvmModifiersOwner;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class SpringHelper {

    public static List<ProjectApiBean> getAllRequest(Project project){
        Module[] modules = ModuleManager.getInstance(project).getModules();
        return Arrays.stream(modules).map(module -> getSpringRequestByModule(project,module)).flatMap(List::stream).collect(Collectors.toList());
    }

    @NotNull
    public static List<ProjectApiBean> getSpringRequestByModule(@NotNull Project project, @NotNull Module module) {
        List<ProjectApiBean> moduleList = new ArrayList<>(0);

        List<PsiClass> controllers = getAllControllerClass(project, module);
        if (controllers.isEmpty()) {
            return moduleList;
        }

        for (PsiClass controllerClass : controllers) {
            moduleList.addAll(getRequests(controllerClass));
        }
        return moduleList;
    }

    @NotNull
    public static List<ProjectApiBean> getRequests(@NotNull PsiClass psiClass) {
        if (!hasRestful(psiClass)){
            return Collections.emptyList();
        }
        List<ProjectApiBean> requests = new ArrayList<>();
        PsiMethod[] psiMethods = psiClass.getAllMethods();
        final PsiAnnotation parentAnnotation = getRequestMapping(psiClass);
        String parentUri = getRequestMappingUri(parentAnnotation);
        for (PsiMethod psiMethod : psiMethods) {
            final PsiAnnotation requestMapping = getRequestMapping(psiMethod);
            if(Objects.isNull(requestMapping)){
                continue;
            }
            requests.addAll(getRequests(parentUri,requestMapping,psiMethod));
        }
        return requests;
    }

    private static String getRequestMappingUri(PsiAnnotation annotation) {
        if (Objects.isNull(annotation)){
            return "";
        }
        final PsiNameValuePair attributeValue = getAttributeValue(annotation);
        if (Objects.isNull(attributeValue)){
            return "";
        }
        return attributeValue.getLiteralValue();
    }

    public static PsiNameValuePair getAttributeValue(PsiAnnotation annotation) {
        final PsiNameValuePair path = AnnotationUtil.findDeclaredAttribute(annotation, "path");
        if (Objects.isNull(path)){
            return AnnotationUtil.findDeclaredAttribute(annotation, "value");
        }
        return path;
    }

    public static boolean hasRestful(@NotNull PsiClass psiClass) {
        return psiClass.hasAnnotation(Control.Controller.getQualifiedName()) || psiClass.hasAnnotation(Control.RestController.getQualifiedName());
    }

    public static PsiAnnotation getRequestMapping(@NotNull PsiJvmModifiersOwner psiElement) {
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
     * 获取所有的控制器类
     *
     * @param project project
     * @param module  module
     * @return Collection<PsiClass>
     */
    @NotNull
    private static List<PsiClass> getAllControllerClass(@NotNull Project project, @NotNull Module module) {
        List<PsiClass> allControllerClass = new ArrayList<>();

        GlobalSearchScope moduleScope = ProjectConfigUtil.getModuleScope(module);
        Collection<PsiAnnotation> pathList = JavaAnnotationIndex.getInstance().get(
                Control.Controller.getName(),
                project,
                moduleScope
        );
        pathList.addAll(JavaAnnotationIndex.getInstance().get(
                Control.RestController.getName(),
                project,
                moduleScope
        ));
        for (PsiAnnotation psiAnnotation : pathList) {
            PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
            PsiElement psiElement = psiModifierList.getParent();

            if (!(psiElement instanceof PsiClass)) {
                continue;
            }

            PsiClass psiClass = (PsiClass) psiElement;
            allControllerClass.add(psiClass);
        }
        return allControllerClass;
    }

    /**
     * 获取注解中的参数，生成RequestBean
     *
     *
     * @param parentUri
     * @param annotation annotation
     * @return list
     */
    @NotNull
    private static List<ProjectApiBean> getRequests(String parentUri, @NotNull PsiAnnotation annotation, @Nullable PsiMethod psiMethod) {
        if (!isRestfulHandler(psiMethod)){
            return Collections.emptyList();
        }
        final String handlerUri = getRequestMappingUri(annotation);
        ProjectApiBean projectApiBean = new ProjectApiBean();
        projectApiBean.setPath(RestfulHelper.formatUrl(parentUri,handlerUri));
        projectApiBean.setPsiElement(psiMethod);
        projectApiBean.setRequestType(RestfulHelper.getRequestType(annotation));
        return Collections.singletonList(projectApiBean);
    }

    public static boolean isRestfulHandler(PsiMethod psiMethod){
        if (Objects.isNull(psiMethod)){
            return false;
        }
        if (Objects.nonNull(psiMethod.getAnnotation(SpringMVCConstant.RequestMapping))){
            return true;
        }
        if (Objects.nonNull(psiMethod.getAnnotation(SpringMVCConstant.GetMapping))){
            return true;
        }
        if (Objects.nonNull(psiMethod.getAnnotation(SpringMVCConstant.PostMapping))){
            return true;
        }
        return false;
    }



    enum Control {

        /**
         * <p>@Controller</p>
         */
        Controller("Controller", "org.springframework.stereotype.Controller"),

        /**
         * <p>@RestController</p>
         */
        RestController("RestController", "org.springframework.web.bind.annotation.RestController");

        private final String name;
        private final String qualifiedName;

        Control(String name, String qualifiedName) {
            this.name = name;
            this.qualifiedName = qualifiedName;
        }

        public String getName() {
            return name;
        }

        public String getQualifiedName() {
            return qualifiedName;
        }
    }

}
