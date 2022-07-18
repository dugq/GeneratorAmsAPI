package com.dugq.util;

import com.dugq.component.common.NotifyComponent;
import com.dugq.pojo.TargetBean;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2021/3/23.
 */
public class TargetUtils {

    public static TargetBean getTargetBean(Editor editor, Project project){
        //获得光标所处的文件，和方法
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if(Objects.isNull(psiFile)){
            NotifyComponent.error("请选择文件！",project);
            throw new RuntimeException();
        }
        PsiClass containingClass = PsiTreeUtil.getParentOfType(psiFile.findElementAt(editor.getCaretModel().getOffset()), PsiClass.class);
        if(Objects.isNull(containingClass)){
            NotifyComponent.error("请选择controller！",project);
            throw new RuntimeException();
        }
        //获取当前方法
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(psiFile.findElementAt(editor.getCaretModel().getOffset()), PsiMethod.class);
        if ((Objects.isNull(containingMethod))){
            NotifyComponent.error("请选择method！",project);
            throw new RuntimeException();
        }
        return new TargetBean(containingClass,containingMethod);
    }

    public static List<TargetBean> getTargetBean2(Editor editor, Project project){
        List<TargetBean> list = new ArrayList<>();
        //获得光标所处的文件，和方法
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if(Objects.isNull(psiFile)){
            NotifyComponent.error("请选择文件！",project);
            throw new RuntimeException();
        }
        PsiClass containingClass = PsiTreeUtil.getParentOfType(psiFile.findElementAt(editor.getCaretModel().getOffset()), PsiClass.class);
        if(Objects.isNull(containingClass)){
            NotifyComponent.error("请选择controller！",project);
            throw new RuntimeException();
        }
        //获取当前方法
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(psiFile.findElementAt(editor.getCaretModel().getOffset()), PsiMethod.class);
        if ((Objects.nonNull(containingMethod))){
            list.add(new TargetBean(containingClass,containingMethod));
        }else{
            PsiMethod[] allMethods = containingClass.getMethods();
            if (allMethods.length==0){
                return list;
            }
            for (PsiMethod method : allMethods) {
                list.add(new TargetBean(containingClass,method));
            }
        }
        return list;
    }

}
