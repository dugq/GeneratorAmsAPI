package com.dugq.ams;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dugq.pojo.AmsApiSearchParam;
import com.dugq.pojo.EditorParam;
import com.dugq.pojo.GroupVo;
import com.dugq.pojo.SimpleApiVo;
import com.dugq.util.ApiParamBuildUtil;
import com.dugq.util.HttpClientUtil;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dugq on 2019/12/26.
 */
public class ApiEditorService {
    private static final String addUrl = "http://ams.dui88.com/server/index.php?g=Web&c=Api&o=addApi";
    private static final String editorUrl = "http://ams.dui88.com/server/index.php?g=Web&c=Api&o=editApi";
    private static final String searchUrl = "http://ams.dui88.com/server/index.php?g=Web&c=Api&o=searchApi";
    private static final String searchGroup = "http://ams.dui88.com/server/index.php?g=Web&c=Group&o=getGroupList";
    private static SerializerFeature[] features = {
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteMapNullValue
    };

    public static void editAPI(Project project, EditorParam param){
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(param,features));
        try {
            JSONObject jsonObject1 = HttpClientUtil.sendPost(editorUrl, jsonObject);
            System.out.println(jsonObject1);
        } catch (IOException e) {
            ApiParamBuildUtil.error("保存失败",project);
            throw new RuntimeException();
        }
    }

    public static void addAPI(Project project, EditorParam param){
        String text = JSON.toJSONString(param, features);
        JSONObject jsonObject = JSONObject.parseObject(text);
        try {
            HttpClientUtil.sendPost(addUrl,jsonObject);
        } catch (IOException e) {
            ApiParamBuildUtil.error("保存失败",project);
        }
    }

    public static List<SimpleApiVo> amsApiSearchParam(Project project, String uri){
        AmsApiSearchParam param = new AmsApiSearchParam();
        param.setTips(uri);
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(param));
        try {
            JSONObject result = HttpClientUtil.sendPost(searchUrl, jsonObject);
            String groupList = result.getString("apiList");
            return JSONArray.parseArray(groupList, SimpleApiVo.class);
        } catch (IOException e) {
            ApiParamBuildUtil.error("搜索失败",project);
            throw new RuntimeException();
        }
    }


    public static List<GroupVo> allGroup(Project project){
        Map<String, Object> params = new HashMap<>();
        params.put("projectID",118);
        params.put("groupID",-1);
        params.put("childGroupID",-1);
        try {
            JSONObject result = HttpClientUtil.sendPost(searchGroup, params);
            String groupList = result.getString("groupList");
            return JSONArray.parseArray(groupList, GroupVo.class);
        } catch (IOException e) {
            ApiParamBuildUtil.error("搜索失败",project);
            throw new RuntimeException();
        }
    }
}
