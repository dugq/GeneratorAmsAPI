package cn.com.duiba.live.normal.service.mybatisgenerator.xmlGenrator;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class SelectOneElementGenrator extends
        AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");

        answer.addAttribute(new Attribute("id","selectById"));

        answer.addAttribute(new Attribute("resultMap","BaseResultMap"));

        answer.addElement(new TextElement("select "));

        XmlElement include = new XmlElement("include");

        include.addAttribute(new Attribute("refid","Base_Column_List"));

        answer.addElement(include);

        answer.addElement(new TextElement("from "+introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        answer.addElement(new TextElement("where id = #{id}"));

        parentElement.addElement(answer);
    }
}
