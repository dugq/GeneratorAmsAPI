package com.dugq.util;

import com.dugq.pojo.TargetBean;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;

import java.util.Objects;

/**
 * Created by dugq on 2021/3/23.
 */
public class TargetUtils {

    public static TargetBean getTargetBean(Editor editor, Project project){
        //获得光标所处的文件，和方法
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if(Objects.isNull(psiFile)){
            ApiParamBuildUtil.error("请选择文件！",project);
            throw new RuntimeException();
        }
        PsiClass containingClass = PsiTreeUtil.getParentOfType(psiFile.findElementAt(editor.getCaretModel().getOffset()), PsiClass.class);
        if(Objects.isNull(containingClass)){
            ApiParamBuildUtil.error("请选择controller！",project);
            throw new RuntimeException();
        }
        //获取当前方法
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(psiFile.findElementAt(editor.getCaretModel().getOffset()), PsiMethod.class);
        if ((Objects.isNull(containingMethod))){
            ApiParamBuildUtil.error("请选择method！",project);
            throw new RuntimeException();
        }
        return new TargetBean(containingClass,containingMethod);
    }

}
