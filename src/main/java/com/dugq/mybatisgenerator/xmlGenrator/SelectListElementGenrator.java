package cn.com.duiba.live.normal.service.mybatisgenerator.xmlGenrator;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class SelectListElementGenrator extends
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

        answer.addAttribute(new Attribute("id","selectList"));

        answer.addAttribute(new Attribute("resultMap","BaseResultMap"));

        answer.addElement(new TextElement("select "));

        XmlElement include = new XmlElement("include");

        include.addAttribute(new Attribute("refid","Base_Column_List"));

        answer.addElement(include);

        answer.addElement(new TextElement("from "+introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement include2 = new XmlElement("include");

        include2.addAttribute(new Attribute("refid","Where_Params"));

        answer.addElement(include2);

        parentElement.addElement(answer);

    }
}
