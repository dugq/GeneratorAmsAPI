package cn.com.duiba.live.normal.service.mybatisgenerator.mapperGenerator;

import cn.com.duiba.live.normal.service.mybatisgenerator.daoGenerator.DeleteGenerator;
import cn.com.duiba.live.normal.service.mybatisgenerator.daoGenerator.InsertGenerator;
import cn.com.duiba.live.normal.service.mybatisgenerator.daoGenerator.SelectByIdGenerator;
import cn.com.duiba.live.normal.service.mybatisgenerator.daoGenerator.SelectCountGenerator;
import cn.com.duiba.live.normal.service.mybatisgenerator.daoGenerator.SelectListGenerator;
import cn.com.duiba.live.normal.service.mybatisgenerator.xmlGenrator.MyXmlGenerator;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByPrimaryKeySelectiveMethodGenerator;
import org.mybatis.generator.config.PropertyRegistry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class MyJavaMapperGenerator extends JavaMapperGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);
        String remarks = introspectedTable.getRemarks();

        StringBuilder sb = new StringBuilder();
        interfaze.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(remarks);
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        interfaze.addJavaDocLine(" * @author kjj mybatis generator ");
        interfaze.addJavaDocLine(" * @date "+ (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()));
        interfaze.addJavaDocLine(" */");

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }

        addSelectList(interfaze);
        addSelectCount(interfaze);
        addSelectOne(interfaze);
        addInsertSelectiveMethod(interfaze);
        addUpdateByPrimaryKeySelectiveMethod(interfaze);
        addDeleted(interfaze);
//        addCountByExampleMethod(interfaze);
//        addDeleteByExampleMethod(interfaze);
//        addDeleteByPrimaryKeyMethod(interfaze);
//        addInsertMethod(interfaze);
//        addInsertSelectiveMethod(interfaze);
//        addSelectByExampleWithBLOBsMethod(interfaze);
//        addSelectByExampleWithoutBLOBsMethod(interfaze);
//        addSelectByPrimaryKeyMethod(interfaze);
//        addUpdateByExampleSelectiveMethod(interfaze);
//        addUpdateByExampleWithBLOBsMethod(interfaze);
//        addUpdateByExampleWithoutBLOBsMethod(interfaze);
//        addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
//        addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().clientGenerated(interfaze, null,
                introspectedTable)) {
            answer.add(interfaze);
        }

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
    }

    private void addSelectOne(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectByIdGenerator(false);
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    private void addSelectList(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectListGenerator(false);
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    protected void addInsertSelectiveMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new InsertGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    private void addSelectCount(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectCountGenerator(false);
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }

    private void addDeleted(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new DeleteGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }

    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            UpdateByPrimaryKeySelectiveMethodGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
            List<Method> methods = interfaze.getMethods();
            methods.forEach(method -> {
                if(Objects.equals(method.getName(),"updateByPrimaryKeySelective")){
                    method.setName("update");
                    FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                            .calculateAllFieldsClass();
                    List<Parameter> parameters = method.getParameters();
                    parameters.remove(0);
                    method.addParameter(new Parameter(parameterType, "entity"));
                    method.addJavaDocLine("/**");
                    method.addJavaDocLine(" * 更新");
                    method.addJavaDocLine(" * @param entity " + introspectedTable.getRemarks().replace("\n", " ").replace("表", ""));
                    method.addJavaDocLine(" * @return 执行结果");
                    method.addJavaDocLine(" */");
                }

            });
        }
    }
    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new MyXmlGenerator();
    }
}
