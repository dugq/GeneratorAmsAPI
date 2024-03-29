package com.dugq.mybatisgenerator.plugin;

import com.dugq.exception.SqlException;
import com.dugq.mybatisgenerator.dtoGenerator.DtoGenerator;
import com.dugq.mybatisgenerator.dtoGenerator.ParamGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.NullProgressCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by dugq on 2018/4/16.
 */
public class MyPluginAdapter extends PluginAdapter {
    private String dtoTargetProject;
    private String paramTargetProject;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        dtoTargetProject = properties.getProperty("dtoTargetProject");
        paramTargetProject = properties.getProperty("paramTargetProject");
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.Data");
        if (CollectionUtils.isEmpty(topLevelClass.getSuperInterfaceTypes()) && topLevelClass.getSuperClass() == null) {
            List<Field> fields = topLevelClass.getFields();
            Field field = fields.get(0);
            if (field != null && "id".equals(field.getName())) {
                List<String> javaDocLines = field.getJavaDocLines();
                javaDocLines.add(0, " ");
            }
        }
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        List<GeneratedJavaFile> list = new ArrayList<>();
        if(Boolean.valueOf(tableConfiguration.getProperty("genDto"))){
            AbstractJavaGenerator javaGenerator = new DtoGenerator();
            javaGenerator.setContext(context);
            javaGenerator.setIntrospectedTable(introspectedTable);
            javaGenerator.setProgressCallback(new NullProgressCallback());
            ArrayList<String> warnings = new ArrayList<>();
            javaGenerator.setWarnings(warnings);
            if(CollectionUtils.isNotEmpty(warnings)){
                throw new SqlException(warnings);
            }
            List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(compilationUnits.get(0), dtoTargetProject,context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),context.getJavaFormatter());
            list.add(generatedJavaFile);
        }
        if (Boolean.valueOf(tableConfiguration.getProperty("genParam"))) {
            AbstractJavaGenerator javaGenerator = new ParamGenerator();
            javaGenerator.setContext(context);
            javaGenerator.setIntrospectedTable(introspectedTable);
            javaGenerator.setProgressCallback(new NullProgressCallback());
            ArrayList<String> warnings = new ArrayList<>();
            javaGenerator.setWarnings(warnings);
            if(CollectionUtils.isNotEmpty(warnings)){
                throw new SqlException(warnings);
            }
            List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(compilationUnits.get(0), paramTargetProject,context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),context.getJavaFormatter());
            list.add(generatedJavaFile);
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if(Objects.equals(introspectedColumn.getJdbcTypeName(),"DATETIME")){
            FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.time.LocalDateTime");
            introspectedColumn.setFullyQualifiedJavaType(type);
            field.setType(type);
            topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
            topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
        }else if (Objects.equals(introspectedColumn.getJdbcTypeName(),"DATE" )){
            topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
            topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
            FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.time.LocalDate");
            introspectedColumn.setFullyQualifiedJavaType(type);
            field.setType(type);
        }else if (Objects.equals(introspectedColumn.getJdbcTypeName(),"TIME" )){
            topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
            topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
            FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.time.LocalTime");
            introspectedColumn.setFullyQualifiedJavaType(type);
            field.setType(type);
        }
//        if(!introspectedColumn.isNullable() && !topLevelClass.getType().getShortName().endsWith("Dto")){
//            field.addAnnotation("@NotNull");
//            topLevelClass.addImportedType("javax.validation.constraints.NotNull");
//        }
//        if(Objects.equals(introspectedColumn.getJdbcTypeName(),"DATETIME")||Objects.equals(introspectedColumn.getJdbcTypeName(),"DATE" )|| Objects.equals(introspectedColumn.getJdbcTypeName(),"TIME" )){
//            field.addAnnotation("@JsonFormat(pattern=\"yyyy-MM-dd HH:mm:ss\",timezone = \"GMT+8\")");
//            field.addAnnotation("@DateTimeFormat(pattern=\"yyyy-MM-dd\")");
//        }
//        if(Objects.equals(introspectedColumn.getJdbcTypeName(),"VARCHAR")&& !topLevelClass.getType().getShortName().endsWith("Dto")){
//            field.addAnnotation("@Length(max = "+introspectedColumn.getLength()+",message=\""+introspectedColumn.getRemarks()+"最大长度为："+introspectedColumn.getLength()+"\")");
//            topLevelClass.addImportedType("org.hibernate.validator.constraints.Length");
//        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }



    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return super.clientCountByExampleMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }



}
