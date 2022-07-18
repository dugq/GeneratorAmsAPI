package com.dugq.service.yapi;

/**
 * @author dugq
 * @date 2021/8/11 4:33 下午
 */
public interface UrlFactory {

     String host = "https://yapi.kjjcrm.com";

      String loginUrl = "/api/user/login";

      String ldapLoginUrl = "/api/user/login_by_ldap";

    /**
     * 空间列表
     */
    String groupUrl = "/api/group/list";

    /**
     * 项目列表
     */
    String projectUrl = "/api/project/list";

    String addProjectPermissionUrl = "/api/project/add_member";

    /**
     * api列表
     */
    String projectApiListUrl = "/api/interface/list";

    //内容搜索API
    String searchUrl = "/api/project/search";

    //新建APi
    String addApiUrl = "/api/interface/add";


    String getApiDetail = "/api/interface/get";

    //编辑APi
    String editApiUrl = "/api/interface/up";

    //添加分类
    String addMenu = "/api/interface/add_cat";

    //分类列表
    String listMenu = "/api/interface/list_menu";
}
