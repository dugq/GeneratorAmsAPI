package com.dugq.service.ams;

import com.alibaba.fastjson.JSONObject;
import com.dugq.component.ams.AmsLoginFormComponent;
import com.dugq.component.common.NotifyComponent;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.ams.UserInfo;
import com.dugq.util.HttpClientUtil;
import com.dugq.util.Md5Util;
import com.dugq.util.XmlUtil;
import com.intellij.openapi.project.Project;
import com.twmacinta.util.MD5;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
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
    private static String PHPSESSID;

    public static String getPHPSESSID(){
        if (StringUtils.isBlank(PHPSESSID)){
            throw new ErrorException("ams未登陆");
        }
        return PHPSESSID;
    }

    public static void login(Project project){
        UserInfo userInfo = getUserInfo(project);
        String PHPSESSID = MD5.asHex(RandomUtils.nextBytes(8));
        Map<String,Object> map = new HashMap<>();
        try {
            map.put("loginName",userInfo.getAccount());
            map.put("loginPassword", Md5Util.toMD5String(userInfo.getPassword()));
            JSONObject jsonObject = HttpClientUtil.sendPost(loginUrl, map,"PHPSESSID="+PHPSESSID);
            if("000000".equals(jsonObject.getString("statusCode"))){
                LoginService.PHPSESSID = PHPSESSID;
            }else{
                NotifyComponent.error("ams登陆错误",project);
                clearUserInfo();
                throw new RuntimeException();
            }
        } catch (IOException e) {
            NotifyComponent.error("ams登陆错误",project);
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
        AmsLoginFormComponent component = new AmsLoginFormComponent();
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

    public static void checkLogin(Project project){
        try {
            JSONObject jsonObject = HttpClientUtil.sendPost(checkLogin, new HashMap<>(),"PHPSESSID="+PHPSESSID);
            if(!"000000".equals(jsonObject.getString("statusCode"))){
                NotifyComponent.error("账号密码错误",project);
                LoginService.PHPSESSID = null;
            }
        } catch (IOException e) {
            NotifyComponent.error("ams登陆错误",project);
            throw new RuntimeException();
        }
    }

    public static UserInfo readUserInfo(Project project) {
        try {
            String path = project.getProjectFile().getParent().getPath();
            return XmlUtil.getInfoFromXml(path+"/ams.xml");
        }catch (Exception e){
            throw new ErrorException("idea配置文件未加载完成，请重试");
        }
    }

    public static void writeUserInfo(Project project, UserInfo userInfo) {
        String path = project.getProjectFile().getParent().getPath();
        XmlUtil.write(userInfo.getAccount(),userInfo.getPassword(),path+"/ams.xml");
    }

    public static void clearUserInfo() {
        URL resource = LoginService.class.getResource("/main/dataBase");
        try (
                FileWriter writer = new FileWriter(new File(resource.getFile()),false);
        ){
            InputStream path = LoginService.class.getResourceAsStream("/main/dataBase");
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
