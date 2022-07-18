package com.dugq.service.config;

import com.alibaba.fastjson.JSON;
import com.dugq.exception.ErrorException;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author dugq
 * @date 2021/7/8 8:34 下午
 */
public abstract  class AbstractMultiValueConfigService<T> implements MultiValueConfigService<T>{

    private final String filePath;
    private final Project project;

    public AbstractMultiValueConfigService(String filePath, Project project) {
        this.filePath = filePath;
        this.project = project;
    }

    protected void writeList(List<T> saveObj) {
        File file = getFile();
        if (file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(file))
        ){
            printWriter.print(JSON.toJSONString(saveObj));
        } catch (FileNotFoundException e) {
            throw new ErrorException("保存失败。文件不存在");
        }
    }

    @NotNull
    private File getFile() {
        String path = project.getProjectFile().getParent().getPath();
        File file = new File(path + filePath);
        return file;
    }

    public List<T> readList(Class<T> clazz){
        final File file = getFile();
        if (!file.exists()){
            return null;
        }
        try (
                FileReader reader = new FileReader(file)
        ){
            String saveBeanJSON = IOUtils.toString(reader);
            return JSON.parseArray(saveBeanJSON,clazz);
        }catch (Exception e){
            throw new ErrorException("read config error!");
        }
    }





}
