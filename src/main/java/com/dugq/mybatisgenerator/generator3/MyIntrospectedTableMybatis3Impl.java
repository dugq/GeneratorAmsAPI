package com.dugq.mybatisgenerator.generator3;

import com.dugq.mybatisgenerator.mapperGenerator.MyJavaMapperGenerator;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.javamapper.AnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.MixedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.codegen.mybatis3.model.RecordWithBLOBsGenerator;
import org.mybatis.generator.internal.ObjectFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2018/9/3 0003.
 */
public class MyIntrospectedTableMybatis3Impl extends IntrospectedTableMyBatis3Impl {

    private static final String SUB_PACKAGE = "subPackage";

    /** The client generators. */

    @Override
    protected void calculateModelAttributes() {
        StringBuilder sb = new StringBuilder();
        String modelPackage = calculateJavaModelPackage();
        sb.append(modelPackage);
        String subPackage = tableConfiguration.getProperty(SUB_PACKAGE);
        if(StringUtils.isNotBlank(subPackage)){
            sb.append('.');
            sb.append(subPackage);
        }
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Key");
        setPrimaryKeyType(sb.toString());

        sb.setLength(0);
        sb.append(modelPackage);
        if(StringUtils.isNotBlank(subPackage)){
            sb.append('.');
            sb.append(subPackage);
        }
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Entity");
        setBaseRecordType(sb.toString());

        sb.setLength(0);
        sb.append(modelPackage);
        if(StringUtils.isNotBlank(subPackage)){
            sb.append('.');
            sb.append(subPackage);
        }
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("WithBLOBs");
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(modelPackage);
        if(StringUtils.isNotBlank(subPackage)){
            sb.append('.');
            sb.append(subPackage);
        }
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Example");
        setExampleType(sb.toString());
    }
    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<>();

        if (xmlMapperGenerator != null) {
            Document document = xmlMapperGenerator.getDocument();
            List<Attribute> attributes = document.getRootElement().getAttributes();
            for(int i =0; i< attributes.size();i++){
                Attribute attribute = attributes.get(i);
                if(attribute.getName().equalsIgnoreCase("namespace")){
                    String value = attribute.getValue();
                    String pre = value.substring(0, value.lastIndexOf("."));
                    String fix =  value.substring(value.lastIndexOf(".")+1);
//                    String namespcace = pre + ".impl." + fix + "Impl";
                    try {
                        Class<? extends Attribute> aClass = attribute.getClass();
                        Field field = aClass.getDeclaredField("value");
                        field.setAccessible(true);
//                        field.set(attribute,namespcace);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            String myBatis3XmlMapperPackage = getMyBatis3XmlMapperPackage();
            String subPackage = tableConfiguration.getProperty(SUB_PACKAGE);
            if(StringUtils.isNotBlank(subPackage)){
                myBatis3XmlMapperPackage = myBatis3XmlMapperPackage+"."+subPackage;
            }
            GeneratedXmlFile gxf = new GeneratedXmlFile(document,
                    getMyBatis3XmlMapperFileName(), myBatis3XmlMapperPackage,
                    context.getSqlMapGeneratorConfiguration().getTargetProject(),
                    true, context.getXmlFormatter());
            if (context.getPlugins().sqlMapGenerated(gxf, this)) {
                answer.add(gxf);
            }
        }

        return answer;
    }

    @Override
    protected String calculateJavaClientInterfacePackage() {
        String s = super.calculateJavaClientInterfacePackage();
        String subPakkage = tableConfiguration.getProperty(SUB_PACKAGE);
        if(StringUtils.isNotBlank(subPakkage)){
            s = s+"."+subPakkage;
        }
        return s;
    }

    @Override
    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return null;
        }

        String type = context.getJavaClientGeneratorConfiguration()
                .getConfigurationType();

        AbstractJavaClientGenerator javaGenerator;
        if ("XMLMAPPER".equalsIgnoreCase(type)) {
            javaGenerator = new MyJavaMapperGenerator();
        } else if ("MIXEDMAPPER".equalsIgnoreCase(type)) {
            javaGenerator = new MixedClientGenerator();
        } else if ("ANNOTATEDMAPPER".equalsIgnoreCase(type)) {
            javaGenerator = new AnnotatedClientGenerator();
        } else if ("MAPPER".equalsIgnoreCase(type)) {
            javaGenerator = new JavaMapperGenerator();
        } else {
            javaGenerator = (AbstractJavaClientGenerator) ObjectFactory
                    .createInternalObject(type);
        }
        return javaGenerator;
    }

    @Override
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
        if (this.getRules().generateExampleClass()) {
            AbstractJavaGenerator javaGenerator = new ExampleGenerator();
            this.initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            this.javaModelGenerators.add(javaGenerator);
        }

        if (this.getRules().generatePrimaryKeyClass()) {
            AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
            this.initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            this.javaModelGenerators.add(javaGenerator);
        }

        //重写
        if (this.getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
            this.initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            this.javaModelGenerators.add(javaGenerator);
        }

        if (this.getRules().generateRecordWithBLOBsClass()) {
            AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
            this.initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            this.javaModelGenerators.add(javaGenerator);
        }
    }
}
