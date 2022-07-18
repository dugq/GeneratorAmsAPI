package com.dugq.bean;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.util.PropertiesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dugq
 * @date 2022/7/18 11:25 上午
 */
public class Config {

    private static final Map<String,String> confMap = new HashMap<>();

    static {
        try {
            final InputStream input = Config.class.getClassLoader().getResourceAsStream("conf.properties");
            Reader reader = new InputStreamReader(input);
            confMap.putAll( PropertiesUtil.loadProperties(reader));
            System.out.println(JSON.toJSONString(confMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConfig(String name){
       return confMap.get(name);
    }

}
