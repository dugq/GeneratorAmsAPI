package com.dugq.service;

import com.dugq.exception.ErrorException;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.TargetBean;
import com.dugq.pojo.enums.RequestType;
import com.dugq.requestmapping.param.RPCParamBuildUtil;
import com.dugq.util.SpringMVCConstant;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author dugq
 * @date 2021/7/16 6:07 下午
 */
public class FeignApiService {

    public boolean isFeignApi(PsiClass psiClass){
        if (Objects.isNull(psiClass)){
            return false;
        }
        if (psiClass.hasAnnotation(SpringMVCConstant.AdvancedFeign)){
            return true;
        }
        if (Objects.isNull(psiClass.getSuperClass())){
            return false;
        }
        return psiClass.getSuperClass().hasAnnotation(SpringMVCConstant.AdvancedFeign);
    }



    public ApiBean getFeignBean(TargetBean targetBean, Project project) {
        ApiBean apiBean = new ApiBean();
        apiBean.setApiURI(getFeignUri(targetBean));
        apiBean.setApiRequestType(RequestType.get);
        apiBean.setApiName(targetBean.getContainingClass().getName()+"#"+targetBean.getContainingMethod().getName());
        apiBean.setRpc(true);
        final List<ParamBean> list = RPCParamBuildUtil.getList(targetBean.getContainingMethod(), project);
        apiBean.setFeignParamBean(list);
        return apiBean;
    }

    @NotNull
    private String getFeignUri(TargetBean targetBean) {
        String className = targetBean.getContainingClass().getName();
        String methodName = targetBean.getContainingMethod().getName();
        if (Objects.isNull(className)){
            throw new ErrorException("请选择类");
        }
        return "/" + lowerFirst(className) + "/" + methodName;
    }

    public static String lowerFirst(String oldStr){
        char[]chars = oldStr.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);

    }
}
