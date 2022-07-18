package com.dugq.service.config;

import com.alibaba.fastjson.JSON;
import com.dugq.exception.ErrorException;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author dugq
 * @date 2021/8/11 6:55 下午
 */
public abstract class AbstractSingleValueConfigService<T> implements SingleConfigService<T>{
    private final Project project;
    private final String filePath;
    private final Class<T> clazz;

    protected AbstractSingleValueConfigService(Project project,String filePath,Class<T> clazz) {
        this.project = project;
        this.filePath = filePath;
        this.clazz = clazz;
    }


    @Override
    public void save(T obj) {
        delete();
        File file = getFile();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(file))
        ){
            printWriter.print(JSON.toJSONString(obj));
        } catch (FileNotFoundException e) {
            throw new ErrorException("保存失败");
        }

    }

    @Override
    public void delete() {
        final File file = getFile();
        if (file.exists()){
            file.delete();
        }
    }


    @Override
    public T read(){
        final File file = getFile();
        if (!file.exists()){
            return null;
        }
        try (
                FileReader reader = new FileReader(file)
        ){
            String saveBeanJSON = IOUtils.toString(reader);
            return JSON.parseObject(saveBeanJSON,clazz);
        }catch (Exception e){
            throw new ErrorException("read config error!");
        }
    }

    private File getFile(){
        String path = project.getProjectFile().getParent().getPath();
        return new File(path + filePath);
    }

}
