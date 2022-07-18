package com.dugq.pojo;

import com.dugq.exception.ErrorException;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiVariable;
import org.apache.commons.collections.CollectionUtils;

import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/7/6 8:59 上午
 *
 * 我们的目的：
 * 以接口的方法为根结点，以方法的参数以及参数的字段和参数的字段的字段 为子节点构建一个链表。
 * 这样，我们随时随地知道当前进入的节点位置。以及在报错的时候，我们也知道进行到了哪一步。
 *
 */
public class ThreadStack {

    private static final ThreadLocal<PsiMethod> psiMethodThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<Stack<PsiVariable>> varStack = new ThreadLocal<>();


    public static void init(PsiMethod psiMethod){
        psiMethodThreadLocal.set(psiMethod);
        varStack.set(new Stack<>());
    }

    public static void rest(){
        psiMethodThreadLocal.remove();
        varStack.remove();
    }


    public static void pushVar(PsiVariable psiVariable){
        final boolean qiantao = varStack.get().stream().anyMatch(v -> Objects.equals(psiVariable.getType(), v.getType()));
        if (qiantao){
            throw new ErrorException("禁止类型嵌套！");
        }
        varStack.get().push(psiVariable);
    }

    public static boolean popVar(PsiVariable psiVariable){
        if (Objects.equals(psiVariable,varStack.get().peek())){
            varStack.get().pop();
            return true;
        }
        return false;
    }

    public static PsiMethod getMethod(){
        return psiMethodThreadLocal.get();
    }

    public static String getStack(){
        final Stack<PsiVariable> psiVariables = varStack.get();
        if (CollectionUtils.isEmpty(psiVariables)){
            return "";
        }
        return psiVariables.stream().map(PsiVariable::getName).collect(Collectors.joining(" > "));
    }

    public static PsiVariable getSupperVar(){
        return varStack.get().peek();
    }

}
