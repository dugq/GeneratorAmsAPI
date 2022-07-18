package com.dugq.mybatisgenerator.dtoGenerator;

import org.apache.commons.lang3.RandomUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.internal.util.messages.Messages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lizhi
 * @date 2020/3/27 7:51 PM
 */
public class ParamGenerator extends BaseRecordGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = super.introspectedTable.getFullyQualifiedTable();
        super.progressCallback.startTask(Messages.getString("Progress.8", table.toString()));
        Plugin plugins = super.context.getPlugins();
        CommentGenerator commentGenerator = super.context.getCommentGenerator();
        String baseRecordType = super.introspectedTable.getBaseRecordType();
        baseRecordType = baseRecordType.replace("Entity","SearchParam");
        baseRecordType = baseRecordType.replace("entity","api.param");
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(baseRecordType);
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        addSerialVersionUID(topLevelClass);
        commentGenerator.addJavaFileComment(topLevelClass);
        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
            FullyQualifiedJavaType javaType = new FullyQualifiedJavaType("lombok.EqualsAndHashCode");
            topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = true)");
            topLevelClass.addImportedType(javaType);
        }

        commentGenerator.addModelClassComment(topLevelClass, super.introspectedTable);
        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();
        if (super.introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);
            if (!super.introspectedTable.isImmutable()) {
                super.addDefaultConstructor(topLevelClass);
            }
        }

        String rootClass = super.getRootClass();
        Iterator var9 = introspectedColumns.iterator();

        while(var9.hasNext()) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn)var9.next();
            if (!RootClassInfo.getInstance(rootClass, super.warnings).containsProperty(introspectedColumn)) {
                Field field = JavaBeansUtil.getJavaBeansField(introspectedColumn, super.context, super.introspectedTable);
                if (plugins.modelFieldGenerated(field, topLevelClass, introspectedColumn, super.introspectedTable, Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addField(field);
                    topLevelClass.addImportedType(field.getType());
                }
                Method method = JavaBeansUtil.getJavaBeansGetter(introspectedColumn, super.context, super.introspectedTable);
                if (plugins.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, super.introspectedTable, Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }

                if (!super.introspectedTable.isImmutable()) {
                    method = JavaBeansUtil.getJavaBeansSetter(introspectedColumn, super.context, super.introspectedTable);
                    if (plugins.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, super.introspectedTable, Plugin.ModelClassType.BASE_RECORD)) {
                        topLevelClass.addMethod(method);
                    }
                }
            }
        }

        List<CompilationUnit> answer = new ArrayList();
        if (super.context.getPlugins().modelBaseRecordClassGenerated(topLevelClass, super.introspectedTable)) {
            answer.add(topLevelClass);
        }

        return answer;
    }


    private FullyQualifiedJavaType getSuperClass() {
        return new FullyQualifiedJavaType("PageQuery");
    }

    private boolean includePrimaryKeyColumns() {
        return !this.introspectedTable.getRules().generatePrimaryKeyClass() && this.introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !this.introspectedTable.getRules().generateRecordWithBLOBsClass() && this.introspectedTable.hasBLOBColumns();
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        this.context.getCommentGenerator().addGeneralMethodComment(method, this.introspectedTable);
        List<IntrospectedColumn> constructorColumns = this.includeBLOBColumns() ? this.introspectedTable.getAllColumns() : this.introspectedTable.getNonBLOBColumns();
        Iterator var4 = constructorColumns.iterator();

        while(var4.hasNext()) {
            IntrospectedColumn introspectedColumn = (IntrospectedColumn)var4.next();
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }

        StringBuilder sb = new StringBuilder();
        Iterator var6;
        IntrospectedColumn introspectedColumn;
        if (this.introspectedTable.getRules().generatePrimaryKeyClass()) {
            boolean comma = false;
            sb.append("super(");

            for(var6 = this.introspectedTable.getPrimaryKeyColumns().iterator(); var6.hasNext(); sb.append(introspectedColumn.getJavaProperty())) {
                introspectedColumn = (IntrospectedColumn)var6.next();
                if (comma) {
                    sb.append(", ");
                } else {
                    comma = true;
                }
            }

            sb.append(");");
            method.addBodyLine(sb.toString());
        }

        List<IntrospectedColumn> introspectedColumns = this.getColumnsInThisClass();
        var6 = introspectedColumns.iterator();

        while(var6.hasNext()) {
            introspectedColumn = (IntrospectedColumn)var6.next();
            sb.setLength(0);
            sb.append("this.");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = ");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        topLevelClass.addMethod(method);
    }

    private List<IntrospectedColumn> getColumnsInThisClass() {
        List introspectedColumns;
        if (this.includePrimaryKeyColumns()) {
            if (this.includeBLOBColumns()) {
                introspectedColumns = this.introspectedTable.getAllColumns();
            } else {
                introspectedColumns = this.introspectedTable.getNonBLOBColumns();
            }
        } else if (this.includeBLOBColumns()) {
            introspectedColumns = this.introspectedTable.getNonPrimaryKeyColumns();
        } else {
            introspectedColumns = this.introspectedTable.getBaseColumns();
        }

        return introspectedColumns;
    }

    private void addSerialVersionUID(TopLevelClass topLevelClass) {
        Field field = new Field();
        field.addJavaDocLine(" ");
        field.setFinal(true);
        field.setInitializationString(System.currentTimeMillis()+""+ RandomUtils.nextLong(0,10000) +"L"); //$NON-NLS-1$
        field.setName("serialVersionUID"); //$NON-NLS-1$
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long")); //$NON-NLS-1$
        field.setVisibility(JavaVisibility.PRIVATE);
        context.getCommentGenerator().addFieldComment(field, introspectedTable);
        topLevelClass.getFields().add(0,field);
    }
}
