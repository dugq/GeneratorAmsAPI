package com.dugq.util;

import com.dugq.exception.ErrorException;
import com.dugq.pojo.ams.UserInfo;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by dugq on 2020/5/20.
 */
public class XmlUtil {

    public static void write(String name, String password, String filePath) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), format);
            // 生成一个新的Document对象
            Document doc = DocumentHelper.createDocument();
            Element rootElement = doc.addElement("project");

            Element account = rootElement.addElement("account").addAttribute("desc", "账号密码");
            account.addElement("name").addText(name);
            account.addElement("password").addText(password);
            writer.write(doc);
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UserInfo getInfoFromXml(String filePath){
// 创建一个XML解析器对象
        SAXReader reader = new SAXReader();
        // 读取XML文档，返回Document对象
        File file = new File(filePath);
        if (!file.exists()){
            return null;
        }
        try {
            UserInfo userInfo = new UserInfo();
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            Element account = rootElement.element("account");
            String name = account.element("name").getText();
            userInfo.setAccount(name);
            String pass = account.element("password").getText();
            userInfo.setPassword(pass);
            return userInfo;
        } catch (DocumentException e) {
            file.delete();
            throw new ErrorException(null,null,e.getMessage());
        }
    }
}
