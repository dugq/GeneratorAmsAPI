package com.dugq.bean;

import com.dugq.exception.ErrorException;
import com.dugq.util.MyPsiTypesUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/7/5 4:00 下午
 */
public class GenericTool {
    private static char openAngle = '<';
    private static char closeAngle = '>';
    private static char genericSep = ',';

    /**
     * 泛型列表
     * K -> PsiType
     * V -> PsiType
     * 为什么是PsiType？
     * 因为K可能还包含这泛型呢！解决泛型嵌套，让泛型继续向下传递
     */
    private final Map<String, PsiType> extGenericTypes = new HashMap<>();

    /**
     * 泛型必然作用在对象之上，对象必然需要声明，这个就是对象的声明
     */
    private final PsiType psiType;


    public GenericTool(PsiType psiType) {
        this.psiType = psiType;
        final Map<String, PsiType> psiTypeGenericTypes = MyPsiTypesUtils.getPsiTypeGenericTypes(psiType);
        if (MapUtils.isNotEmpty(psiTypeGenericTypes)){
            extGenericTypes.putAll(psiTypeGenericTypes);
        }
    }


    public  static GenericTool create(PsiType psiType){
        return new GenericTool(psiType);
    }

    public static boolean isPsiTypeContainsGeneric(PsiType psiType) {
        return psiType.getCanonicalText().contains("<") && psiType.getCanonicalText().contains(">");
    }

    public GenericTool explainSupperClass(PsiType childType, Project project){
        final PsiType psiType = replaceFiledTypeGeneric(childType,project);
        return create(psiType);
    }

    public PsiType getGenericType(String genericName){
        return extGenericTypes.get(genericName);
    }

    public PsiType getPsiType() {
        return psiType;
    }


    public PsiType getPsiTypeAndDealGeneric(PsiField psiField){
        final PsiType psiType = replaceFiledTypeGeneric(psiField.getType(),psiField.getProject());
        if (Objects.isNull(psiType)){
            throw new ErrorException("变量声明 "+psiField.getType().getCanonicalText()+" "+psiField.getName()+" 的范型类型推倒失败！");
        }
        return psiType;
    }

    public PsiType replaceFiledTypeGeneric(PsiType filedType, Project project){
        //如果是范型，就要修改它的类型为实际类型
        if (isPsiTypeGeneric(filedType)){
            final PsiType genericType = getGenericType(filedType.getCanonicalText());
            if (Objects.isNull(genericType)){
                return null;
            }
            filedType = genericType;
        }
        if (isPsiTypeContainsGeneric(filedType)){
            return new GenericType(filedType.getCanonicalText(),this).getExplainType(project);
        }
        return filedType;
    }


    /**
     * 判断psiType是否是泛型。
     * psiType认为泛型是正常类型。我们通过类型的完全限定名来区分是否泛型。
     * 正常类名的完全限定名理论应该是必然含有.的，切类名长度肯定大于等于3，如果真的类型<3那别怪我出错。写代码的肯定是傻逼。同样也没有哪个傻逼把泛型声明成3个字符的吧。脑子瓦特了吧。
     */
    public static boolean isPsiTypeGeneric(PsiType psiType){
        final String classFullName = psiType.getCanonicalText();
        return isPsiClassNameGeneric(classFullName);
    }

    private static boolean isPsiClassNameGeneric(String classFullName) {
        return StringUtils.isBlank(classFullName) || (classFullName.length() < 3 && !classFullName.contains("."));
    }

    class GenericType{
        private GenericTool genericTool;
        private String psiClassName;
        private List<GenericType> genericList = new ArrayList<>();

        public GenericType(String psiTypeCanonicalText, GenericTool genericTool) {
            this.genericTool = genericTool;
            if (!psiTypeCanonicalText.contains(String.valueOf(openAngle))){
                this.psiClassName = psiTypeCanonicalText.trim();
                return;
            }
            //List<A<B,C<D>>,E<G>,F>
            final int firstOpenAngle = psiTypeCanonicalText.indexOf(openAngle);
            this.psiClassName = psiTypeCanonicalText.substring(0,firstOpenAngle);
            String genericStatement = psiTypeCanonicalText.substring(firstOpenAngle+1,psiTypeCanonicalText.length()-1);

            //A<B,C<D>>,E<G>,F
            int mark = 0;
            int openTimes = 0;
            int i = 0;
            for ( ; i< genericStatement.length();i++){
                if (genericStatement.charAt(i)==openAngle){
                    openTimes++;
                }
                if (genericStatement.charAt(i)==closeAngle){
                    openTimes--;
                }
                if (genericStatement.charAt(i)==genericSep && openTimes==0){
                    final String substring = genericStatement.substring(mark, i);
                    genericList.add(new GenericType(substring,genericTool));
                    mark = i+1;
                }
            }
            genericList.add(new GenericType(genericStatement.substring(mark,i),genericTool));
        }



        public PsiType getExplainType(Project project){
            StringBuilder psiClassType = new StringBuilder() ;
            if (isPsiClassNameGeneric(psiClassName)){
                psiClassType.append(genericTool.getGenericType(psiClassName).getCanonicalText());
            }else{
                psiClassType.append(psiClassName);
            }
            if (CollectionUtils.isEmpty(genericList)){
                return MyPsiTypesUtils.createPsiTypeByName(psiClassType.toString(),project);
            }
            psiClassType.append(openAngle);
            final String genericTypeName = genericList.stream().map(genericType -> genericType.getExplainType(project).getCanonicalText()).collect(Collectors.joining(","));
            psiClassType.append(genericTypeName);
            psiClassType.append(closeAngle);
            return MyPsiTypesUtils.createPsiTypeByName(psiClassType.toString(),project);
        }
    }
}
