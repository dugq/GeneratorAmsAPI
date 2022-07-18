package cn.com.duiba.live.normal.service.mybatisgenerator.daoGenerator;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class SelectByIdGenerator  extends
        AbstractJavaMapperMethodGenerator {
    private boolean isSimple;

    public SelectByIdGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }


    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType returnType = introspectedTable.getRules()
                .calculateAllFieldsClass();
        method.setReturnType(returnType);
        importedTypes.add(returnType);

        method.setName("selectById");
        importedTypes.add(new FullyQualifiedJavaType("java.lang.Long"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.Long"),"id"));
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 根据主键查询");
        method.addJavaDocLine(" * @param id 主键");
        method.addJavaDocLine(" * @return " + introspectedTable.getRemarks().replace("\n", " ").replace("表", ""));
        method.addJavaDocLine(" */");

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        if (context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }
}