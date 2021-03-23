package com.dugq.ams;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dugq.exception.ErrorException;
import com.dugq.pojo.AmsApiSearchParam;
import com.dugq.pojo.EditorParam;
import com.dugq.pojo.GroupVo;
import com.dugq.pojo.SimpleApiVo;
import com.dugq.util.ApiParamBuildUtil;
import com.dugq.util.ApiUtils;
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

    public static void uploadAPI(Project project, EditorParam param, List<GroupVo> groupVos){
        if (param.getType()==1){
            editAPI(project,param);
        }else{
            GroupVo groupVo = ApiUtils.getGroupVo(groupVos,param.getApiURI());
            param.setGroupID(groupVo.getGroupID());
            addAPI(project,param);
        }
    }

    public static void editAPI(Project project, EditorParam param){
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(param,features));
        try {
            JSONObject jsonObject1 = HttpClientUtil.sendPost(editorUrl, jsonObject);
            System.out.println(jsonObject1);
        } catch (IOException e) {
            throw new ErrorException(null,null,"链接ams错误");
        }
    }

    public static void addAPI(Project project, EditorParam param){
        String text = JSON.toJSONString(param, features);
        JSONObject jsonObject = JSONObject.parseObject(text);
        try {
            HttpClientUtil.sendPost(addUrl,jsonObject);
            ApiParamBuildUtil.success("上传接口:"+param.getApiURI()+".成功",project);
        } catch (IOException e) {
            throw new ErrorException(null,null,"链接ams错误");
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
            throw new ErrorException(null,null,"链接ams错误");
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
            throw new ErrorException(null,null,"链接ams错误");
        }
    }
}
