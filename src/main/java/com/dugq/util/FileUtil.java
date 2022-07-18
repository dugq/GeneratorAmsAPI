package com.dugq.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.PropertiesUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * Created by dugq on 2021/4/20.
 */
public class FileUtil {

    public static String getDefaultPort(Project project){
        PsiFile[] filesByName = FilenameIndex.getFilesByName(project, "application.properties", GlobalSearchScope.projectScope(project));
        if (ArrayUtils.isEmpty(filesByName)){
            return "8080";
        }
        PsiFile psiFile = filesByName[0];
        String text = psiFile.getText();
        Reader reader = new StringReader(text);
        try {
            Map<String, String> properties = PropertiesUtil.loadProperties(reader);
            String port = properties.get("server.port");
            return StringUtils.isBlank(port)?"8080":port;
        } catch (IOException e) {
            return "8080";
        }
    }
}
