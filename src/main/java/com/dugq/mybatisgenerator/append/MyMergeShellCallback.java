package com.dugq.mybatisgenerator.append;

import com.dugq.exception.SqlException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2022/7/6 2:07 下午
 */
public class MyMergeShellCallback extends DefaultShellCallback {
    private Project project;

    /**
     * Instantiates a new default shell callback.
     */
    public MyMergeShellCallback(Project project) {
        super(false);
        this.project = project;
    }


    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding) throws ShellException {
        return myMergeJavaFile(newFileSource, existingFileFullPath, javadocTags, fileEncoding).getText();
    }

    public PsiJavaFile myMergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding) throws ShellException {
        final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(existingFileFullPath));
        if (Objects.isNull(virtualFile)){
            throw new SqlException("文件："+existingFileFullPath+"读取错误");
        }
        final PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
        if (Objects.isNull(file) || !(file instanceof PsiJavaFile)){
            throw new SqlException("文件："+virtualFile.getName()+"格式错误");
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile)file;
        final PsiClass[] classes = psiJavaFile.getClasses();
        final PsiClass oldClass = classes[0];


        final PsiFile newPsiFile = PsiFileFactory.getInstance(project).createFileFromText(psiJavaFile.getName(), psiJavaFile.getFileType(), newFileSource);
        PsiJavaFile newPsiJavaFile = (PsiJavaFile)newPsiFile;
        final PsiClass newClass = newPsiJavaFile.getClasses()[0];

        final Map<String,PsiField> oldFieldMap = Arrays.stream(oldClass.getAllFields()).collect(Collectors.toMap(PsiField::getName, Function.identity(), (left, right) -> {
            throw new SqlException("field name 重复" + left.getName());
        }));

        final Map<String,PsiField> newFieldMap = Arrays.stream(newClass.getAllFields()).collect(Collectors.toMap(PsiField::getName, Function.identity(), (left, right) -> {
            throw new SqlException("field name 重复" + left.getName());
        }));

        final @NotNull PsiClass[] singleClassImports = newPsiJavaFile.getSingleClassImports(false);
        for (PsiClass singleClassImport : singleClassImports) {
            psiJavaFile.importClass(singleClassImport);
        }
        for (String newFiled : newFieldMap.keySet()) {
            if (oldFieldMap.containsKey(newFiled)){
                continue;
            }
            final PsiField psiField = newFieldMap.get(newFiled);
            oldClass.add(psiField.copy());
        }

        final Map<String,PsiMethod> oldMethodMap = Arrays.stream(oldClass.getMethods()).collect(Collectors.toMap(getMethodOnlyFlag(), Function.identity(), (left, right) -> {
            throw new SqlException("method name 重复" + left.getName());
        }));

        final Map<String,PsiMethod> newMethodMap = Arrays.stream(newClass.getMethods()).collect(Collectors.toMap(getMethodOnlyFlag(), Function.identity(), (left, right) -> {
            throw new SqlException("method name 重复" + left.getName());
        }));


        for (String newMethod : newMethodMap.keySet()) {
            if (oldMethodMap.containsKey(newMethod)){
                continue;
            }
            final PsiMethod psiMethod = newMethodMap.get(newMethod);
            final PsiElement copy = psiMethod.copy();
            oldClass.add(copy);
        }
        CodeStyleManager.getInstance(project).reformat(oldClass);
        return psiJavaFile;
    }

    @NotNull
    public Function<@NotNull PsiMethod, String> getMethodOnlyFlag() {
        return method -> {
            if (method.getParameterList().isEmpty()) {
                return method.getName();
            } else {
                return method.getName() + "-" + Arrays.stream(method.getParameterList().getParameters()).map(p -> p.getType().getCanonicalText()).collect(Collectors.joining("-"));
            }
        };
    }
}
