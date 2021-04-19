package com.dugq.service;

import com.alibaba.fastjson.JSON;
import com.dugq.bean.TestAPIGlobalSettingBean;
import com.dugq.component.TestApiPanel;
import com.dugq.util.TestApiUtil;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * Created by dugq on 2021/4/8.
 */
public class SaveTestAPIGlobalService{


    public static void init(Project project, TestApiPanel testApiPanel){
        String path = project.getProjectFile().getParent().getPath();
        String filePath = path + "/globalSetting.txt";
        File file = new File(filePath);
        if (!file.exists()){
            return;
        }
        try {
            FileReader reader = new FileReader(file);
            String saveBeanJSON = IOUtils.toString(reader);
            TestAPIGlobalSettingBean testAPIGlobalSettingBean = JSON.parseObject(saveBeanJSON,TestAPIGlobalSettingBean.class);
            if (Objects.isNull(testAPIGlobalSettingBean)){
                return;
            }
            testApiPanel.setHost(testAPIGlobalSettingBean.getHost());

            if (MapUtils.isNotEmpty(testAPIGlobalSettingBean.getGlobalParam())){
                testAPIGlobalSettingBean.getGlobalParam().forEach(testApiPanel::addGlobalParam);
            }

            if (MapUtils.isNotEmpty(testAPIGlobalSettingBean.getHeaderMap())){
                testAPIGlobalSettingBean.getHeaderMap().forEach(testApiPanel::addHeader);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void save(Project project){
        TestApiPanel testApiPanel = TestApiUtil.getTestApiPanel(project);
        TestAPIGlobalSettingBean testAPIGlobalSettingBean = new TestAPIGlobalSettingBean();

        testAPIGlobalSettingBean.setHost(testApiPanel.getHost());
        testAPIGlobalSettingBean.setGlobalParam(testApiPanel.getGlobalParamMap());
        testAPIGlobalSettingBean.setHeaderMap(testApiPanel.getHeaderMap());
        String saveBeanJSON = JSON.toJSONString(testAPIGlobalSettingBean);
        saveGlobalSetting(project,saveBeanJSON);
    }

    private static void saveGlobalSetting(Project project,String setting) {
        String path = project.getProjectFile().getParent().getPath();
        String filePath = path + "/globalSetting.txt";
        File file = new File(filePath);
        file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(filePath))
            ){
            printWriter.print(setting);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static SaveTestAPIGlobalService getInstance(Project project){
        return project.getService(SaveTestAPIGlobalService.class);
    }




}
