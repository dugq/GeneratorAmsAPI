package com.dugq.service.yapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dugq.bean.ResponseBean;
import com.dugq.component.common.CenterSelectDialog;
import com.dugq.component.common.MyClickButton;
import com.dugq.exception.ErrorException;
import com.dugq.exception.StopException;
import com.dugq.pojo.ApiBean;
import com.dugq.pojo.ParamBean;
import com.dugq.pojo.enums.ParamTypeEnum;
import com.dugq.pojo.enums.RequestType;
import com.dugq.pojo.yapi.ResultBean;
import com.dugq.pojo.yapi.YapiMenuBean;
import com.dugq.pojo.yapi.YapiSearchResult;
import com.dugq.pojo.yapi.api.AddApiParam;
import com.dugq.pojo.yapi.api.ApiQueryBean;
import com.dugq.pojo.yapi.api.EditApiResult;
import com.dugq.pojo.yapi.api.JSONBodyBean;
import com.dugq.pojo.yapi.api.SimpleApiBean;
import com.dugq.pojo.yapi.api.SimpleListApiResult;
import com.dugq.pojo.yapi.api.YapiApiModel;
import com.dugq.pojo.yapi.api.YapiParamType;
import com.dugq.service.config.impl.YapiConfigService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/8/11 9:16 下午
 */
public class YapiInterfaceService extends YapiBaseService{
    private final Project project;
    private final YaiSearchService yaiSearchService;
    private final YapiProjectService yapiProjectService;
    private final YapiConfigService configService;
    private final YapiMenuService yapiMenuService;

    public YapiInterfaceService(Project project) {
        this.project = project;
        yaiSearchService = project.getService(YaiSearchService.class);
        yapiProjectService = project.getService(YapiProjectService.class);
        configService = project.getService(YapiConfigService.class);
        yapiMenuService = project.getService(YapiMenuService.class);
    }

    public YapiSearchResult.Api searchByPath11(String path,long projectId){
        final YapiSearchResult searchResult = yaiSearchService.search(path);
        final List<YapiSearchResult.Api> api = searchResult.getApi();
        if (CollectionUtils.isEmpty(api)){
            return null;
        }
        final List<YapiSearchResult.Api> projectApis = api.stream().filter(a -> a.getProjectId() == projectId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(projectApis)){
            return null;
        }
        // 缺少path不能比对结果
        throw new UnsupportedOperationException("缺少path不能比对结果 此方法暂不支持");
    }


    public SimpleApiBean searchByPath(String path, long projectId){
        Map<String,String> params = new HashMap<>();
        params.put("project_id",String.valueOf(projectId));
        params.put("page","1");
        params.put("limit","10000");
        try {
            final ResponseBean responseBean = sendGet(UrlFactory.projectApiListUrl, params);
            final String responseBody = responseBean.getResponseBody();
            ResultBean<SimpleListApiResult> result = JSON.parseObject(responseBody, new TypeReference<ResultBean<SimpleListApiResult>>(){});
            if (!result.isSuccess()){
                throw new ErrorException("查询api列表失败 errorcode="+result.getErrcode()+"errmsg="+result.getErrmsg());
            }
            final List<SimpleApiBean> list = result.getData().getList();
            if (CollectionUtils.isEmpty(list)){
                return null;
            }
            final List<SimpleApiBean> pathList = list.stream().filter(a -> StringUtils.equals(path, a.getPath())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(pathList)){
                return null;
            }
            if (pathList.size()>1){
                throw new ErrorException("path="+path+"有多个API，这很不合逻辑");
            }
            return pathList.get(0);
        } catch (IOException e) {
            throw new ErrorException("查询接口失败");
        }
    }


    @Override
    Project getProject() {
        return project;
    }

    public String upload(ApiBean apiUploadBean){
        return upload(apiUploadBean,false);
    }
    /**
     * 上传API
     * 1、从配置中读取当前项目ID
     * 2、如果配置为空，选择项目
     *
     * 3、根据path查询API_Id
     * 4、如果是查询不到，先选择分类，新增API，获取到API_ID
     * 5、查询API，替换部分参数后 保存API
     * @return 接口访问地址
     */
    public String upload(ApiBean apiUploadBean,boolean supportUpdate) {
        long projectId = getProjectId();
        long apiId = getApiId(apiUploadBean,projectId,supportUpdate);
        editApi(apiUploadBean,apiId);
        return UrlFactory.host+"/project/"+projectId+"/interface/api/"+apiId;
    }

    private void editApi(ApiBean apiUploadBean, long apiId) {
        YapiApiModel yapiApiBean = findApiDetail(apiId);
        yapiApiBean.setTitle(apiUploadBean.getApiName());
        yapiApiBean.setDesc(apiUploadBean.getDesc());
        yapiApiBean.setMethod(apiUploadBean.getApiRequestType().getDesc());
        if (apiUploadBean.getApiRequestType()== RequestType.get){
           List<ApiQueryBean>  queryBeans= changeParamList2QueryBean(apiUploadBean.getApiParamBean());
            yapiApiBean.setReqQuery(queryBeans);
        }else{
            final List<ParamBean> apiParamBean = apiUploadBean.getApiParamBean();
            if (CollectionUtils.isNotEmpty(apiParamBean)) {
                JSONBodyBean queryBeans= changeParamList2ReqBodyBean(apiParamBean.get(0).getChildren());
                yapiApiBean.setReqBody("json");
                yapiApiBean.setJson(true);
                yapiApiBean.setReqBody(JSON.toJSONString(queryBeans));
            }
        }
        dealEmptyHeaders(yapiApiBean);
        JSONBodyBean resultBody = changeParamList2ReqBodyBean(apiUploadBean.getApiResultParam());
        yapiApiBean.setResBody(JSON.toJSONString(resultBody));
        yapiApiBean.setResBodyIsJson(true);
        yapiApiBean.setStatus("done");

        try {
            System.out.println(JSON.toJSONString(yapiApiBean));
            final ResponseBean responseBean = sendPost(UrlFactory.editApiUrl, yapiApiBean);
            if (!responseBean.isSuccess()){
                throw new ErrorException("保存接口调用失败:"+responseBean.getStatus());
            }
            final ResultBean<EditApiResult> data = responseBean.getObjectData(EditApiResult.class);
            if (!data.isSuccess()){
                throw new ErrorException("保存接口失败:"+data.getErrmsg());
            }
            if (data.getData().getOk()!=1){
                throw new ErrorException("保存接口失败:未知原因");
            }
        } catch (IOException e) {
            throw new ErrorException("保存接口连接失败");
        }
    }

    private void dealEmptyHeaders(YapiApiModel yapiApiBean) {
        final List<Header> headers = yapiApiBean.getHeaders();
        final List<Header> newHeaders = headers.stream().filter(header -> StringUtils.isNotBlank(header.getName())).collect(Collectors.toList());
        yapiApiBean.setHeaders(newHeaders);
    }

    private JSONBodyBean changeParamList2ReqBodyBean(List<ParamBean> apiParamBean) {
        if (CollectionUtils.isEmpty(apiParamBean)){
            return null;
        }
        return doGeneratorReqBodyBean("empty object",apiParamBean,null);
    }

    private JSONBodyBean doGeneratorReqBodyBean(String tile, List<ParamBean> children, String desc){
        JSONBodyBean reqBodyBean = JSONBodyBean.of(tile,YapiParamType.OBJECT,desc);
        if (CollectionUtils.isEmpty(children)){
            return reqBodyBean;
        }
        for (ParamBean paramBean : children) {
            final YapiParamType paramType = YapiParamType.getTypeParamType(paramBean.getParamType());
            if (Objects.equals(paramType,YapiParamType.OBJECT)){
                reqBodyBean.addProps(doGeneratorReqBodyBean(paramBean.getParamKey(),paramBean.getChildren(),paramBean.getParamName()),paramBean.getParamNotNull()==0);
            }else if (Objects.equals(paramType,YapiParamType.ARRAY)){
                JSONBodyBean arrayReq = getArrayReqBodyBean(paramBean.getParamKey(), desc, paramBean);
                reqBodyBean.addProps(arrayReq,paramBean.getParamNotNull()==0);
            }else{
                JSONBodyBean simpleBean = JSONBodyBean.of(paramBean.getParamKey(),paramType,paramBean.getParamName());
                simpleBean.addEnums(paramBean.getParamValueList());
                simpleBean.setDefaultValue(paramBean.getParamValue());
                reqBodyBean.addProps(simpleBean,paramBean.getParamNotNull()==0);
            }
        }
        return reqBodyBean;
    }

    private JSONBodyBean getArrayReqBodyBean(String tile, String desc, ParamBean paramBean) {
        JSONBodyBean arrayReq = JSONBodyBean.of(tile,YapiParamType.ARRAY, desc);
        final ParamTypeEnum childType = paramBean.getChildType();
        if (childType.isNormalType()){
            JSONBodyBean items = JSONBodyBean.of("items",YapiParamType.getTypeParamType(childType), desc);
            arrayReq.setItems(items);
        }else{
            final List<ParamBean> thisChild = paramBean.getChildren();
            JSONBodyBean items = doGeneratorReqBodyBean("items",thisChild,null);
            arrayReq.setItems(items);
        }
        return arrayReq;
    }


    private List<ApiQueryBean> changeParamList2QueryBean(List<ParamBean> apiParamBean) {
        if (CollectionUtils.isEmpty(apiParamBean)){
            return null;
        }
        List<ApiQueryBean> queryBeans = new ArrayList<>();
        for (ParamBean paramBean : apiParamBean) {
            final ParamTypeEnum paramType = paramBean.getParamType();
            if (ParamTypeEnum.OBJECT==paramType){
                final List<ApiQueryBean> childrenBeans = changeParamList2QueryBean(paramBean.getChildren());
                queryBeans.addAll(childrenBeans);
            }else if(ParamTypeEnum.ARRAY==paramType){
                System.out.println("11111");
            }else{
                ApiQueryBean normalQuery = new  ApiQueryBean();
                normalQuery.setDesc(paramBean.getParamName());
                normalQuery.setExample(paramBean.getParamValue());
                normalQuery.setName(paramBean.getParamKey());
                normalQuery.setRequired(paramBean.getParamNotNull());
                queryBeans.add(normalQuery);
            }
        }
        return queryBeans;
    }



    private YapiApiModel findApiDetail(long apiId) {
        Map<String,String> map = new HashMap<>();
        map.put("id",String.valueOf(apiId));
        try {
            final ResponseBean responseBean = sendGet(UrlFactory.getApiDetail, map);
            if (!responseBean.isSuccess()){
                throw new ErrorException("查询API详情失败");
            }
            ResultBean<YapiApiModel> result = JSON.parseObject(responseBean.getResponseBody(), new TypeReference<ResultBean<YapiApiModel>>(){});
            if (!result.isSuccess()){
                throw new ErrorException("查询API详情失败，请重试");
            }
            return result.getData();
        } catch (IOException e) {
            throw new ErrorException("查询API详情失败");
        }
    }

    private long getProjectId() {
       return yapiProjectService.getCurrentProjectId();
    }


    private long getApiId(ApiBean apiUploadBean,long projectId,boolean supportUpdate) {
        SimpleApiBean simpleApiBean = searchByPath(apiUploadBean.getApiURI(),projectId);
        if (Objects.nonNull(simpleApiBean)){
            if (supportUpdate){
                return simpleApiBean.getId();
            }
            String detail = "接口描述："+simpleApiBean.getTitle()
                    +"\n url: "+ simpleApiBean.getPath();
            int update = Messages.showDialog(detail, "存在相同uri接口，是否更新？", new String[]{"是", "否"}, 0, null);
            if (update==0){
                return simpleApiBean.getId();
            }else{
                throw new StopException();
            }
        }
        return addApi(apiUploadBean,projectId);
    }

    private long addApi(ApiBean apiUploadBean, long projectId) {
        final Long defaultMenuId = apiUploadBean.getMenuId();
        final Long menuId = Objects.isNull(defaultMenuId)?getMenuId(projectId):defaultMenuId;
        AddApiParam addApiParam = new AddApiParam();
        addApiParam.setMenuId(menuId);
        addApiParam.setMethod(apiUploadBean.getApiRequestType().getDesc().toUpperCase());
        addApiParam.setPath(apiUploadBean.getApiURI());
        addApiParam.setTitle(apiUploadBean.getApiName());
        addApiParam.setProjectId(projectId);
        try {
            final ResponseBean responseBean = sendPost(UrlFactory.addApiUrl, addApiParam);
            if (!responseBean.isSuccess()){
                throw new ErrorException("新建API失败 response status="+responseBean.getStatus());
            }
            ResultBean<YapiApiModel> result = JSON.parseObject(responseBean.getResponseBody(), new TypeReference<ResultBean<YapiApiModel>>(){});
            if (!result.isSuccess()){
                throw new ErrorException("查询api列表失败 errorcode="+result.getErrcode()+"errmsg="+result.getErrmsg());
            }
            return result.getData().getId();
        } catch (IOException e) {
            throw new ErrorException("新建API失败");
        }
    }

    @NotNull
    private Long getMenuId(long projectId) {
        final List<YapiMenuBean> menuList = yapiMenuService.getMenuList(projectId);
        final CenterSelectDialog<YapiMenuBean> menuDialog = CenterSelectDialog.getInstance("请选择接口分类",menuList,YapiMenuBean::getName,null);
        final MyClickButton createMenuButton = new MyClickButton("新建分类", event -> {
            menuDialog.close(-1);
        }, 100);
        menuDialog.setCustomButton(createMenuButton);
        final int existCode = menuDialog.showAndGetExistCode();
        if (existCode!=CenterSelectDialog.OK_EXIT_CODE){
            if (existCode==-1){
                return yapiMenuService.createMenu(projectId);
            }
            throw new StopException();
        }
        return menuDialog.getLastSelect().getId();
    }

}
