package com.dugq.util;

import com.dugq.pojo.ParamSelectValue;
import com.dugq.pojo.enums.YapiStatusEnum;
import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述工具
 *
 * @author chengsheng@qbb6.com
 * @date 2019/4/30 4:13 PM
 */
public class DesUtil {


    /**
     * 去除字符串首尾出现的某个字符.
     * @param source 源字符串.
     * @param element 需要去除的字符.
     * @return String.
     */
    public static String trimFirstAndLastChar(String source,char element) {
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do {
            if(Strings.isNullOrEmpty(source.trim()) || source.equals(String.valueOf(element))){
                source="";
                break;
            }
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }


    /**
     * @description: 获得描述
     * @param: [psiMethodTarget]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/2/2
     */
    public static String getDescription(PsiMethod psiMethodTarget){
        if(psiMethodTarget.getDocComment()!=null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if (psiDocTag.getText().contains("@description") || psiDocTag.getText().contains("@Description")) {
                    return trimFirstAndLastChar(psiDocTag.getText().replace("@description", "").replace("@Description", "").replace(":", "").replace("*", "").replace("\n", " "), ' ');
                }
            }
            return trimFirstAndLastChar(psiMethodTarget.getDocComment().getText().split("@")[0].replace("@description", "").replace("@Description", "").replace(":", "").replace("*", "").replace("/", "").replace("\n", " "), ' ').trim();
        }
        return null;
    }
    /**
     * @description: 获得描述
     * @param: [psiMethodTarget]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/2/2
     */
    public static String getReturn(PsiMethod psiMethodTarget){
        if(psiMethodTarget.getDocComment()!=null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if (psiDocTag.getText().contains("@Return") || psiDocTag.getText().contains("@return")) {
                    return trimFirstAndLastChar(psiDocTag.getText().replace("@return", "").replace("@Return", "").replace(":", "").replace("*", "").replace("\n", " "), ' ');
                }
            }
            return trimFirstAndLastChar(psiMethodTarget.getDocComment().getText().split("@")[0].replace("@Return", "").replace("@return", "").replace(":", "").replace("*", "").replace("/", "").replace("\n", " "), ' ').trim();
        }
        return null;
    }


    /**
     * @description: 通过paramName 获得描述
     * @param: [psiMethodTarget, paramName]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/22
     */
    public static String getParamDesc(PsiMethod psiMethodTarget, String paramName){
        if(psiMethodTarget.getDocComment()!=null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if ((psiDocTag.getText().contains("@param") || psiDocTag.getText().contains("@Param")) && (!psiDocTag.getText().contains("[")) && psiDocTag.getText().contains(paramName)) {
                    String trim = trimFirstAndLastChar(psiDocTag.getText().replace("@param", "").replace("@Param", "").replace(paramName, "").replace(":", "").replace("*", "").replace("\n", " "), ' ').trim();
                    if(trim.contains("@link")){
                        return trim.substring(0,trim.indexOf("{@link")).split("@Exp")[0];
                    }else{
                        return trim.split("@Exp")[0];
                    }
                }
            }
        }
        return "";
    }

    public static String getParamExp(PsiMethod psiMethodTarget, String paramName){
        if(psiMethodTarget.getDocComment()!=null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if ((psiDocTag.getText().contains("@param") || psiDocTag.getText().contains("@Param")) && (!psiDocTag.getText().contains("[")) && psiDocTag.getText().contains(paramName)) {
                    String trim = trimFirstAndLastChar(psiDocTag.getText().replace("@param", "").replace("@Param", "").replace(paramName, "").replace(":", "").replace("*", "").replace("\n", " "), ' ').trim();
                    if(trim.contains("@Exp")){
                        trim = subStringWithReg(trim,"@Exp .*");
                        trim = trim.replace("@Exp","").trim();
                        if(trim.contains(" ")){
                            trim = trim.split(" ")[0].trim();
                        }
                        if(StringUtils.isNotBlank(trim)){
                            return trim;
                        }
                    }
                }
            }
        }
        return "";
    }



    public static List<ParamSelectValue> getParamEnumValues(PsiMethod psiMethodTarget, String paramName,Project project){
        if(psiMethodTarget.getDocComment()!=null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if ((psiDocTag.getText().contains("@param") || psiDocTag.getText().contains("@Param")) && (!psiDocTag.getText().contains("[")) && psiDocTag.getText().contains(paramName)) {
                    String trim = trimFirstAndLastChar(psiDocTag.getText().replace("@param", "").replace("@Param", "").replace(paramName, "").replace(":", "").replace("*", "").replace("\n", " "), ' ').trim();
                    List<ParamSelectValue> list = getSelectValuesFromLink(project, trim);
                    if (list != null) return list;
                }
            }
        }
        return null;
    }

    private static List<ParamSelectValue> getSelectValuesFromLink(Project project, String trim) {
        if(trim.contains("@link")){
            trim = subStringWithReg(trim, "@link.*?}").trim();
            trim = trim.replace("@link","").replace("}","").trim();
            List<ParamSelectValue> list = getSelectValueFromPsiClass(project, trim);
            if (list != null) return list;
        }
        return null;
    }

    @Nullable
    private static List<ParamSelectValue> getSelectValueFromPsiClass(Project project, String trim) {
        PsiClass psiClassChild = JavaPsiFacade.getInstance(project).findClass(trim, GlobalSearchScope.allScope(project));
        if(Objects.isNull(psiClassChild)){
            return Collections.emptyList();
        }
        if(psiClassChild.isEnum()){
            PsiField[] allFields = psiClassChild.getAllFields();
            List<ParamSelectValue> list = new ArrayList<>();
            for (PsiField filed : allFields) {
                if(filed instanceof PsiEnumConstant){
                    PsiExpressionList argumentList = ((PsiEnumConstant) filed).getArgumentList();
                    if(Objects.isNull(argumentList)){
                        continue;
                    }
                    PsiExpression[] expressions = argumentList.getExpressions();
                    if(Objects.nonNull(expressions) && expressions.length>1){
                        ParamSelectValue selectValue = new ParamSelectValue();
                        selectValue.setValue(expressions[0].getText());
                        selectValue.setValueDescription(expressions[1].getText());
                        list.add(selectValue);
                    }
                }
            }
            return list;
        }
        return null;
    }

    private static String subStringWithReg(String source,String reg){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(source);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }


    public static List<ParamSelectValue> getFieldEnumValues(Project project,PsiDocComment psiDocComment){
        if(Objects.isNull(psiDocComment)){
            return null;
        }
        String fileText = psiDocComment.getText();
        if(StringUtils.isBlank(fileText)){
            return null;
        }
        List<ParamSelectValue> values = getSelectValuesFromLink(project, fileText);
        if(Objects.isNull(values)){
            String trim = subStringWithReg(fileText, "@see.*?\n");
            if(StringUtils.isNotBlank(trim)){
                trim = trim.replace("@see","").trim();
                trim = trim.split(" ")[0];
                return getSelectValueFromPsiClass(project, trim);
            }
        }else{
            return values;
        }
        return null;
    }
    /**
     * @description: 获得属性注释
     * @param: [psiDocComment]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/4/27
     */
    public static String getFiledDesc(PsiDocComment psiDocComment){
        if(Objects.nonNull(psiDocComment)) {
            String fileText = psiDocComment.getText();
            if (!Strings.isNullOrEmpty(fileText)) {
                if(fileText.contains("@Exp")){
                    fileText = fileText.substring(0,fileText.indexOf("@Exp"));
                }
                return trimFirstAndLastChar(fileText.replace("*", "").replace("/", "").replace(" ", "").replace("\n", ",").replace("\t", ""), ',').split("\\{@link")[0].split("@see")[0];
            }
        }
        return "";
    }

    public static String getFiledDefaultValue(PsiDocComment psiDocComment){
        if(Objects.nonNull(psiDocComment)) {
            String fileText = psiDocComment.getText();
            if(StringUtils.isNotEmpty(fileText) && (fileText.contains("@Exp"))){
                String substring = fileText.substring(fileText.indexOf("@Exp"));
                if(StringUtils.isNotEmpty(substring)){
                    substring= substring.substring(0,substring.indexOf("\n"));
                    String[] split = substring.split("@Exp");
                    if(split.length>1){
                        substring = split[1].trim();
                        if(substring.contains("*")){
                            substring = substring.split("\\*")[0];
                            return substring;
                        }else{
                            return substring;
                        }
                    }

                }
            }
        }
        return null;
    }
    /**
     * @description: 获得引用url
     * @param: []
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */
    public static String getUrlReFerenceRDesc(String text){
        if(Strings.isNullOrEmpty(text)){
            return text;
        }
        if(!text.contains("*/")){
            return null;
        }
        return DesUtil.trimFirstAndLastChar(text.split("\\*/")[0].replace("@description","").replace("@Description","").split("@")[0].replace(":","").replace("*","").replace("/","").replace("\n"," "),' ');
    }

    /**
     * @description: 获得菜单
     * @param: [text]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */
    public static String getMenu(String text) {
        if (Strings.isNullOrEmpty(text) || !text.contains("*/")) {
            return null;
        }
        String[] menuList = text.split("\\*/")[0].split("@menu");
        if (menuList.length > 1) {
            return DesUtil.trimFirstAndLastChar(menuList[1].split("\\*")[0].replace("*", "").replace(":","").replace("\n", " ").replace(" ",""), ' ').trim();
        } else {
            return null;
        }
    }

    /**
     * @description: 获得状态
     * @param: [text]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */
    public static String getStatus(String text) {
        if (Strings.isNullOrEmpty(text) || !text.contains("*/")) {
            return null;
        }
        String[] menuList = text.split("\\*/")[0].split("@status");
        if (menuList.length > 1) {
            return YapiStatusEnum.getStatus(DesUtil.trimFirstAndLastChar(menuList[1].split("\\*")[0].replace("*", "").replace(":","").replace("\n", " ").replace(" ",""), ' ').trim());
        } else {
            return null;
        }
    }

    /**
     * @description: 获得link 备注
     * @param: [remark, project, field]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */
    public static String getLinkRemark(String remark, Project project, PsiField field){
        // 尝试获得@link 的常量定义
        if(Objects.isNull(field.getDocComment())){
            return remark;
        }
        String[] linkString=field.getDocComment().getText().split("@link");
        if(linkString.length>1){
            //说明有link
            String linkAddress=linkString[1].split("}")[0].trim();
            PsiClass psiClassLink= JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
            if(Objects.isNull(psiClassLink)) {
                //可能没有获得全路径，尝试获得全路径
                String[] importPaths=field.getParent().getContext().getText().split("import");
                if(importPaths.length>1){
                    for(String importPath:importPaths){
                        importPath=importPath.split(";")[0];
                        if(importPath.contains(linkAddress.split("\\.")[0])){
                            linkAddress=importPath.split(linkAddress.split("\\.")[0])[0]+linkAddress;
                            psiClassLink= JavaPsiFacade.getInstance(project).findClass(linkAddress.trim(), GlobalSearchScope.allScope(project));
                            break;
                        }
                    }
                }
                if(Objects.isNull(psiClassLink)){
                    //如果是同包情况
                    linkAddress= ((PsiJavaFileImpl) ((PsiClassImpl) field.getParent()).getContext()).getPackageName()+"."+linkAddress;
                    psiClassLink= JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
                }
                //如果小于等于一为不存在import，不做处理
            }
            if(Objects.nonNull(psiClassLink)){
                //说明获得了link 的class
                PsiField[] linkFields= psiClassLink.getFields();
                if(linkFields.length>0){
                    remark+=","+psiClassLink.getName()+"[";
                    for (int i=0;i<linkFields.length;i++){
                        PsiField psiField=linkFields[i];
                        if(i>0){
                            remark+=",";
                        }
                        // 先获得名称
                        remark+=psiField.getName();
                        // 后获得value,通过= 来截取获得，第二个值，再截取;
                        String[] splitValue = psiField.getText().split("=");
                        if(splitValue.length>1){
                            String value=splitValue[1].split(";")[0];
                            remark+=":"+value;
                        }
                        String filedValue= DesUtil.getFiledDesc(psiField.getDocComment());
                        if(!Strings.isNullOrEmpty(filedValue)){
                            remark+="("+filedValue+")";
                        }
                    }
                    remark+="]";
                }
            }
        }
        return remark;
    }


    /**
     * @description: 获得从start 开始 end 结束中间的内容
     * @param: [content, start, end]
     * @return: java.util.List<java.lang.String>
     * @author: chengsheng@qbb6.com
     * @date: 2019/7/2
     */
    public static List<PsiClass> getFieldLinks(Project project, PsiField field){
        if(Objects.isNull(field.getDocComment())){
            return new ArrayList<>();
        }
       List<PsiClass> result=new ArrayList<>();
       String[] linkstr=field.getDocComment().getText().split("@link");
       for(int i=1;i<linkstr.length;i++){
           try {
               String linkAddress = linkstr[i].split("}")[0].trim();
               PsiClass psiClassLink = JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
               if (Objects.isNull(psiClassLink)) {
                   //可能没有获得全路径，尝试获得全路径
                   String[] importPaths = field.getParent().getContext().getText().split("import");
                   if (importPaths.length > 1) {
                       for (String importPath : importPaths) {
                           importPath = importPath.split(";")[0];
                           if (importPath.contains(linkAddress.split("\\.")[0])) {
                               linkAddress = importPath.split(linkAddress.split("\\.")[0])[0] + linkAddress;
                               psiClassLink = JavaPsiFacade.getInstance(project).findClass(linkAddress.trim(), GlobalSearchScope.allScope(project));
                               if (Objects.nonNull(psiClassLink)) {
                                   result.add(psiClassLink);
                               }
                               break;
                           }
                       }
                   }
                   if (Objects.isNull(psiClassLink)) {
                       //如果是同包情况
                       linkAddress = ((PsiJavaFileImpl) ((PsiClassImpl) field.getParent()).getContext()).getPackageName() + "." + linkAddress;
                       psiClassLink = JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
                       if (Objects.nonNull(psiClassLink)) {
                           result.add(psiClassLink);
                       }
                   }
                   //如果小于等于一为不存在import，不做处理
               } else {
                   result.add(psiClassLink);
               }
           }catch (Exception e){
           }
       }
       return result;
    }

    /**
     * @description: 组装路径
     * @param: [path, subPath]
     * @return: void
     * @author: chengsheng@qbb6.com
     * @date: 2019/9/25
     */
    public static void addPath(StringBuilder path,String subPath){
        if(subPath.startsWith("/")){
            path.append(subPath);
        }else{
            path.append("/").append(subPath);
        }
    }
}
