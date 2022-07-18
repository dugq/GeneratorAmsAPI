package com.dugq.pojo.mybatis;

/**
 * @author dugq
 * @date 2022/6/29 12:26 上午
 */
public class ConfigBean {
    //xml 的路径
    private String mapperPath;
    //实体entity的路径
    private String entityPath;
    //dao的路径
    private String daoPath;
    //e.g:jdbc:mysql://rm-bp19h19f5976q5ahg.mysql.rds.aliyuncs.com:3306/kjy_live_normal
    private String dbUrl;
    //数据库用户名
    private String dbUserName;
    //数据库密码
    private String dbPwd;

    //多模块项目，这里可以指定模块。否则有问题
    private String subModule;


    public String getSubModule() {
        return subModule;
    }

    public void setSubModule(String subModule) {
        this.subModule = subModule;
    }

    public String getMapperPath() {
        return mapperPath;
    }

    public void setMapperPath(String mapperPath) {
        this.mapperPath = mapperPath;
    }

    public String getEntityPath() {
        return entityPath;
    }

    public void setEntityPath(String entityPath) {
        this.entityPath = entityPath;
    }

    public String getDaoPath() {
        return daoPath;
    }

    public void setDaoPath(String daoPath) {
        this.daoPath = daoPath;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }
}
