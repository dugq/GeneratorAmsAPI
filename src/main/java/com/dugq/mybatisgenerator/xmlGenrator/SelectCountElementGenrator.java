package com.dugq.mybatisgenerator.xmlGenrator;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class SelectCountElementGenrator extends
        AbstractXmlElementGenerator {

    private Set<String> ingoreClumns = new HashSet<>();

    {
        ingoreClumns.add("id");
        ingoreClumns.add("gmt_create");
        ingoreClumns.add("gmt_modified");

    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");

        answer.addAttribute(new Attribute("id","selectCount"));

        answer.addAttribute(new Attribute("resultType","java.lang.Long"));

        answer.addElement(new TextElement("select count(*)"));
        answer.addElement(new TextElement("from "+introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        XmlElement include2 = new XmlElement("include");

        include2.addAttribute(new Attribute("refid","Where_Params"));

        answer.addElement(include2);
        parentElement.addElement(answer);
    }
}
