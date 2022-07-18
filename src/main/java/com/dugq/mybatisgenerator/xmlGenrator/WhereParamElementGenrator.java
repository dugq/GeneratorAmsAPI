package com.dugq.mybatisgenerator.xmlGenrator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lizhi
 * @date 2020/3/27 8:07 PM
 */
public class WhereParamElementGenrator extends
        AbstractXmlElementGenerator {
    private Set<String> ingoreClumns = new HashSet<>();

    {
        ingoreClumns.add("id");
        ingoreClumns.add("gmt_create");
        ingoreClumns.add("gmt_modified");

    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("sql");

        answer.addAttribute(new Attribute("id","Where_Params"));
        addWhere(answer);

        parentElement.addElement(answer);

    }

    public void addWhere(XmlElement parentElement){
        XmlElement where = new XmlElement("where");
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        allColumns.forEach(column->{
            String clumnName = MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(column);
            if(ingoreClumns.contains(clumnName)){
                return;
            }
            XmlElement ifElement = new XmlElement("if");
            ifElement.addAttribute(new Attribute("test",column.getJavaProperty()+"!=null"));
            ifElement.addElement(new TextElement(" and " + clumnName +"=#{" + column.getJavaProperty()+"}" ));
            where.addElement(ifElement);
        });
        parentElement.addElement(where);
    }
}
