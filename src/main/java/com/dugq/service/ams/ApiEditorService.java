package com.dugq.service.ams;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dugq.component.ams.SelectComponent;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.ams.AmsApiSearchParam;
import com.dugq.pojo.ams.ApiDetail;
import com.dugq.pojo.ams.EditorParam;
import com.dugq.pojo.ams.GroupVo;
import com.dugq.pojo.ams.SimpleApiVo;
import com.dugq.util.APIPrintUtil;
import com.dugq.util.HttpClientUtil;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dugq on 2019/12/26.
 */
public class ApiEditorService {
    private static final String addUrl = "http://ams.dui88.com/server/index.php?g=Web&c=Api&o=addApi";
    private static final String editorUrl = "http://ams.dui88.com/server/index.php?g=Web&c=Api&o=editApi";
    private static final String searchUrl = "http://ams.dui88.com/server/index.php?g=Web&c=Api&o=searchApi";
    private static final String allUrl = "http://ams.dui88.com/server/index.php?g=Web&c=Api&o=getAllApiList";
    private static final String searchGroup = "http://ams.dui88.com/server/index.php?g=Web&c=Group&o=getGroupList";
    private static final String getApi = "http://ams.dui88.com/server/index.php?g=Web&c=Api&o=getApi";



    private static SerializerFeature[] features = {
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteMapNullValue
    };

    public static void uploadAPI(Project project, EditorParam param, List<GroupVo> groupVos){
        if (param.getType()==1){
            editAPI(param,project);
        }else{
            GroupVo groupVo = SelectComponent.getGroupVo(groupVos,param.getApiURI());
            param.setGroupID(groupVo.getGroupID());
            addAPI(project,param);
        }
    }

    public static void editAPI(EditorParam param, Project project){
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(param,features));
        System.out.println(JSON.toJSONString(param.getApiResultParam()));
        try {
            JSONObject resultJson = HttpClientUtil.sendPost(editorUrl, jsonObject,"PHPSESSID="+LoginService.getPHPSESSID());
            if (Objects.isNull(resultJson) || resultJson.get("statusCode")=="000000"){
                APIPrintUtil.printErrorLine("上传AMS失败",project);
                return;
            }
            APIPrintUtil.getAmsToolPanel(project).appendLine("接口AMS地址", ConsoleViewContentType.SYSTEM_OUTPUT).append(getAmsPageUrl(resultJson),ConsoleViewContentType.USER_INPUT);
            APIPrintUtil.show(project);
        } catch (IOException e) {
            throw new ErrorException("ams连接不上");
        }
    }

    @NotNull
    private static String getAmsPageUrl(JSONObject resultJson) {
        return "http://ams.dui88.com/#/home/project/inside/api/detail?projectID=118&groupID=" + resultJson.get("groupID") + "&apiID=" + resultJson.get("apiID");
    }

    public static void addAPI(Project project, EditorParam param){
        String text = JSON.toJSONString(param, features);
        JSONObject jsonObject = JSONObject.parseObject(text);
        try {
            final JSONObject resultJson = HttpClientUtil.sendPost(addUrl, jsonObject, "PHPSESSID=" + LoginService.getPHPSESSID());
            System.out.println(resultJson);
            if (Objects.isNull(resultJson) || resultJson.get("statusCode")=="000000"){
                APIPrintUtil.printErrorLine("上传AMS失败",project);
                return;
            }
            APIPrintUtil.getAmsToolPanel(project).appendLine("接口AMS地址", ConsoleViewContentType.SYSTEM_OUTPUT).append(getAmsPageUrl(resultJson),ConsoleViewContentType.USER_INPUT);
            APIPrintUtil.show(project);
        } catch (IOException e) {
            throw new ErrorException(null,null,"链接ams错误");
        }
    }

    public static List<SimpleApiVo> amsApiSearchParam( String uri){
        AmsApiSearchParam param = new AmsApiSearchParam();
        param.setTips(uri);
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(param));
        try {
            JSONObject result = HttpClientUtil.sendPost(searchUrl, jsonObject,"PHPSESSID="+LoginService.getPHPSESSID());
            String groupList = result.getString("apiList");
            return JSONArray.parseArray(groupList, SimpleApiVo.class);
        } catch (IOException e) {
            throw new ErrorException(null,null,"链接ams错误");
        }
    }

    public static List<SimpleApiVo> amsAllApi(){
        Map<String, Object> params = new HashMap<>();
        params.put("projectID",118);
        params.put("groupID",-1);
        params.put("orderBy",1);
        params.put("asc",1);
        try {
            JSONObject result = HttpClientUtil.sendPost(allUrl, params,"PHPSESSID="+LoginService.getPHPSESSID());
            String groupList = result.getString("apiList");
            return JSONArray.parseArray(groupList, SimpleApiVo.class);
        } catch (IOException e) {
            throw new ErrorException(null,null,"链接ams错误");
        }
    }

    public static ApiDetail getApiDetail(Integer apiId){
        Map<String, Object> params = new HashMap<>();
        params.put("projectID",118);
        params.put("groupID",-1);
        params.put("apiID",apiId);
        try {
            JSONObject result = HttpClientUtil.sendPost(getApi, params,"PHPSESSID="+LoginService.getPHPSESSID());
            String groupList = result.getString("apiInfo");
            return JSONObject.parseObject(groupList, ApiDetail.class);
        } catch (IOException e) {
            throw new ErrorException(null,null,"链接ams错误");
        }
    }


    public static List<GroupVo> allGroup(){
        Map<String, Object> params = new HashMap<>();
        params.put("projectID",118);
        params.put("groupID",-1);
        params.put("childGroupID",-1);
        try {
            JSONObject result = HttpClientUtil.sendPost(searchGroup, params,"PHPSESSID="+LoginService.getPHPSESSID());
            String groupList = result.getString("groupList");
            return JSONArray.parseArray(groupList, GroupVo.class);
        } catch (IOException e) {
            throw new ErrorException(null,null,"链接ams错误");
        }
    }
}
