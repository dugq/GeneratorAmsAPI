package cn.com.duiba.live.normal.service.mybatisgenerator.daoGenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class DeleteGenerator extends
        AbstractJavaMapperMethodGenerator {

    @Override
    public void addInterfaceElements(Interface interfaze) {

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        Method method = new Method();

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("delete");

        FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                .calculateAllFieldsClass();

        importedTypes.add(parameterType);
        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        method.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.Long"), "id"));

        if(hasDeleted() || hasLogicDeleted()){
            method.addJavaDocLine("/**");
            method.addJavaDocLine(" * 软删");
            method.addJavaDocLine(" * @param id 主键");
            method.addJavaDocLine(" * @return 执行结果");
            method.addJavaDocLine(" */");
        } else {

            method.addJavaDocLine("/**");
            method.addJavaDocLine(" * 物理删除");
            method.addJavaDocLine(" * @param id 主键");
            method.addJavaDocLine(" * @return 执行结果");
            method.addJavaDocLine(" */");
        }


        if (context.getPlugins().clientInsertSelectiveMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    private boolean hasDeleted(){
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn allColumn : allColumns) {
            if (allColumn.getActualColumnName().equalsIgnoreCase("deleted")) {
                return true;
            }
        }
        return false;
    }



    private boolean hasLogicDeleted(){
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn allColumn : allColumns) {
            if (allColumn.getActualColumnName().equalsIgnoreCase("logic_deleted")) {
                return true;
            }
        }
        return false;
    }
}
