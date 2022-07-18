package org.mybatis.generator.internal;

import com.dugq.exception.SqlException;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.dom.DOMText;
import org.dom4j.io.SAXReader;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.exception.ShellException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author dugq
 * @date 2022/6/29 5:06 下午
 * 覆盖org.mybatis.generator.internal.XmlFileMergerJaxp，把xml builder替换了
 */
public class XmlFileMergerJaxp {

    private static class NullEntityResolver implements EntityResolver {
        /**
         * returns an empty reader. This is done so that the parser doesn't
         * attempt to read a DTD. We don't need that support for the merge and
         * it can cause problems on systems that aren't Internet connected.
         */
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {

            StringReader sr = new StringReader(""); //$NON-NLS-1$

            return new InputSource(sr);
        }
    }

    /**
     * Utility class - no instances allowed
     */
    private XmlFileMergerJaxp() {
        super();
    }

    public static String getMergedSource(GeneratedXmlFile generatedXmlFile, File existingFile) throws ShellException {
        try {
            return getMergedSource(new InputSource(new StringReader(generatedXmlFile.getFormattedContent())), existingFile, existingFile.getName());
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new SqlException("已存在的xml文件解析错误");
        }
    }

    public static String getMergedSource(InputSource newFile,
                                         File existingFile, String existingFileName) throws ShellException, DocumentException {

        SAXReader reader = new SAXReader();
        Document existingDocument = reader.read(existingFile);
        SAXReader reader2 = new SAXReader();
        Document newDocument = reader2.read(newFile);
        DocumentType newDocType = newDocument.getDocType();
        DocumentType existingDocType = existingDocument.getDocType();

        if (!newDocType.getName().equals(existingDocType.getName())) {
            throw new ShellException(getString("Warning.12", //$NON-NLS-1$
                    existingFileName));
        }

        Element existingRootElement = existingDocument.getRootElement();
        Element newRootElement = newDocument.getRootElement();

        //读取新文件的Mapper节点
        //并解析处所有的方法
        final Map<String, Element> newElementMap = readXmlMapperMap(newRootElement);

        //读取已存在文件的Mapper节点
        //并解析处所有的方法
        final Map<String, Element> existElementMap = readXmlMapperMap(existingRootElement);

        //遍历所有新方法，如果老文件中不存在该方法，则追加。
        for (String newId : newElementMap.keySet()) {
            if (existElementMap.containsKey(newId)){
                continue;
            }
            final Element newElement = newElementMap.get(newId);
            newElement.detach();
            existingRootElement.add(newElement);
            existingRootElement.add(new DOMText("\n"));
        }
        return  existingDocument.asXML();

    }

    public static Map<String, Element> readXmlMapperMap(Element newRootElement) {
        final List<Element> children = newRootElement.elements();
         return children.stream().filter(child -> {
            final Attribute idAttr = child.attribute("id");
            if (Objects.isNull(idAttr)) {
                return false;
            }
            final String value = idAttr.getValue();
            return StringUtils.isNotBlank(value);
        }).collect(Collectors.toMap(child -> child.attribute("id").getValue(), Function.identity(), (left, right) -> {
            throw new SqlException("xml 中ID=" + left.attribute("id").getValue() + "重复！");
        }));
    }
}
