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
public class SelectCountGenerator extends
        AbstractJavaMapperMethodGenerator {
    private boolean isSimple;

    public SelectCountGenerator(boolean isSimple) {
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
        method.setReturnType(new FullyQualifiedJavaType("java.lang.Long"));
        importedTypes.add(returnType);

        method.setName("selectCount");
        FullyQualifiedJavaType paramType = getParam();
        importedTypes.add(paramType);
        method.addParameter(new Parameter(paramType,"param"));
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 根据条件,查询记录的总记录数");
        method.addJavaDocLine(" * @param param 查询条件");
        method.addJavaDocLine(" * @return 符合条件的记录数");
        method.addJavaDocLine(" */");

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        if (context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    private FullyQualifiedJavaType getParam() {
        String baseRecordType = introspectedTable.getBaseRecordType();
        baseRecordType = baseRecordType.replace("Entity","SearchParam");
        baseRecordType = baseRecordType.replace("entity","api.param");
        return new FullyQualifiedJavaType(baseRecordType);
    }
}