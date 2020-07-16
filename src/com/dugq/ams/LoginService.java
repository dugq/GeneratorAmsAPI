package com.dugq.ams;

import com.alibaba.fastjson.JSONObject;
import com.dugq.component.DoubleInputComponent;
import com.dugq.pojo.UserInfo;
import com.dugq.util.ApiParamBuildUtil;
import com.dugq.util.HttpClientUtil;
import com.dugq.util.Md5Util;
import com.dugq.util.XmlUtil;
import com.intellij.openapi.project.Project;
import com.twmacinta.util.MD5;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by dugq on 2019/12/26.
 */
public class LoginService {
    private static final String loginUrl = "http://ams.dui88.com/server/index.php?g=Web&c=Guest&o=login";
    private static final String checkLogin = "http://ams.dui88.com/server/index.php?g=Web&c=Guest&o=checkLogin";

    public static String login(Project project){
        UserInfo userInfo = getUserInfo(project);
        String PHPSESSID = MD5.asHex(RandomUtils.nextBytes(8));
        Map<String,Object> map = new HashMap<>();
        try {
            map.put("loginName",userInfo.getAccount());
            map.put("loginPassword", Md5Util.toMD5String(userInfo.getPassword()));
            JSONObject jsonObject = HttpClientUtil.sendPost(loginUrl, map,"PHPSESSID="+PHPSESSID);
            if("000000".equals(jsonObject.getString("statusCode"))){
                return PHPSESSID;
            }else{
                ApiParamBuildUtil.error("ams登陆错误",project);
                clearUserInfo();
                throw new RuntimeException();
            }
        } catch (IOException e) {
            ApiParamBuildUtil.error("ams登陆错误",project);
            clearUserInfo();
            throw new RuntimeException();
        }
    }

    @NotNull
    private static UserInfo getUserInfo(Project project) {
        UserInfo storeUserInfo = readUserInfo(project);
        if(Objects.nonNull(storeUserInfo)){
            return storeUserInfo;
        }
        DoubleInputComponent component = new DoubleInputComponent();
        boolean result = component.showAndGet();
        if(!result){
           throw new RuntimeException();
        }
        UserInfo userInfo = new UserInfo();
        String account = component.getAccount();
        userInfo.setAccount(account);
        String password = component.getPassword();
        userInfo.setPassword(password);
        writeUserInfo(project,userInfo);
        return userInfo;
    }

    public static Boolean checkLogin(Project project,String PHPSESSID){
        try {
            JSONObject jsonObject = HttpClientUtil.sendPost(checkLogin, new HashMap<>(),"PHPSESSID="+PHPSESSID);
            if("000000".equals(jsonObject.getString("statusCode"))){
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            ApiParamBuildUtil.error("ams登陆错误",project);
            throw new RuntimeException();
        }
    }

    public static UserInfo readUserInfo(Project project) {
        String path = project.getProjectFile().getParent().getPath();
        return XmlUtil.getInfoFromXml(path+"/ams.xml");
    }

    public static void writeUserInfo(Project project, UserInfo userInfo) {
        String path = project.getProjectFile().getParent().getPath();
        XmlUtil.write(userInfo.getAccount(),userInfo.getPassword(),path+"/ams.xml");
    }

    public static void clearUserInfo() {
        URL resource = LoginService.class.getResource("/dataBase");
        try (
                FileWriter writer = new FileWriter(new File(resource.getFile()),false);
        ){
            InputStream path = LoginService.class.getResourceAsStream("/dataBase");
            BufferedReader reader = new BufferedReader(new InputStreamReader(path));
            Properties properties =  new Properties();
            properties.load(reader);
            properties.remove("account");
            properties.remove("password");
            properties.store(writer, "Copyright (c) Boxcode Studio");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
