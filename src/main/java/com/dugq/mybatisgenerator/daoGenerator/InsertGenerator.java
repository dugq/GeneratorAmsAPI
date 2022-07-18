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
public class InsertGenerator extends
        AbstractJavaMapperMethodGenerator {

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        Method method = new Method();

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("insert");

        FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                .calculateAllFieldsClass();

        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "entity"));
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 新增");
        method.addJavaDocLine(" * @param entity " + introspectedTable.getRemarks().replace("\n", " ").replace("表", ""));
        method.addJavaDocLine(" * @return 执行结果");
        method.addJavaDocLine(" */");

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        if (context.getPlugins().clientInsertSelectiveMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }
}
