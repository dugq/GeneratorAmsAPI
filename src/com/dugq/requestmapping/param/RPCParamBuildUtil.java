package com.dugq.requestmapping.param;

import com.dugq.requestmapping.param.bean.ParamBean;
import com.dugq.util.NormalTypes;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dugq on 2021/4/19.
 */
public class RPCParamBuildUtil {

    private RPCParamBuildUtil(){

    }

    public static List<ParamBean> getList(PsiMethod psiMethod){
        PsiParameterList parameterList = psiMethod.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        AtomicInteger index = new AtomicInteger();
        return Stream.of(parameters).map(parameter->{
            ParamBean paramBean = new ParamBean();
            boolean primitive = NormalTypes.isNormalType(parameter.getType().getPresentableText());
            paramBean.setPrimitive(primitive);
            paramBean.setType(parameter.getType().getPresentableText().toLowerCase());
            paramBean.setName(parameter.getName());
            paramBean.setIndex(index.getAndIncrement());
            return paramBean;
        }).collect(Collectors.toList());
    }

}
