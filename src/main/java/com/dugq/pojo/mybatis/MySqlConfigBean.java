package com.dugq.pojo.mybatis;

/**
 * @author dugq
 * @date 2022/6/29 12:26 上午
 */
public class MySqlConfigBean {
    //xml的包路径
    private String mapperPackagePath;
    //xml的根路径
    private String mapperRootPath;
    //实体entity的包路径
    private String entityPackagePath;
    //实体entity的根路径
    private String entityRootPath;
    //dao的路径
    private String daoPackagePath;
    //dao的路径
    private String daoRootPath;
    //e.g:jdbc:mysql://rm-bp19h19f5976q5ahg.mysql.rds.aliyuncs.com:3306/kjy_live_normal
    private String dbUrl;
    //数据库用户名
    private String dbUserName;
    //数据库密码
    private String dbPwd;

    private String genDto;

    private String genParam;

    private String dtoPackagePath;

    private String dtoRootPath;

    private String paramPackagePath;

    private String paramRootPath;

    public String getMapperPackagePath() {
        return mapperPackagePath;
    }

    public void setMapperPackagePath(String mapperPackagePath) {
        this.mapperPackagePath = mapperPackagePath;
    }

    public String getMapperRootPath() {
        return mapperRootPath;
    }

    public void setMapperRootPath(String mapperRootPath) {
        this.mapperRootPath = mapperRootPath;
    }

    public String getEntityPackagePath() {
        return entityPackagePath;
    }

    public void setEntityPackagePath(String entityPackagePath) {
        this.entityPackagePath = entityPackagePath;
    }

    public String getEntityRootPath() {
        return entityRootPath;
    }

    public void setEntityRootPath(String entityRootPath) {
        this.entityRootPath = entityRootPath;
    }

    public String getDaoPackagePath() {
        return daoPackagePath;
    }

    public void setDaoPackagePath(String daoPackagePath) {
        this.daoPackagePath = daoPackagePath;
    }

    public String getDaoRootPath() {
        return daoRootPath;
    }

    public void setDaoRootPath(String daoRootPath) {
        this.daoRootPath = daoRootPath;
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

    public String getGenDto() {
        return genDto;
    }

    public void setGenDto(String genDto) {
        this.genDto = genDto;
    }

    public String getGenParam() {
        return genParam;
    }

    public void setGenParam(String genParam) {
        this.genParam = genParam;
    }

    public String getDtoPackagePath() {
        return dtoPackagePath;
    }

    public void setDtoPackagePath(String dtoPackagePath) {
        this.dtoPackagePath = dtoPackagePath;
    }

    public String getDtoRootPath() {
        return dtoRootPath;
    }

    public void setDtoRootPath(String dtoRootPath) {
        this.dtoRootPath = dtoRootPath;
    }

    public String getParamPackagePath() {
        return paramPackagePath;
    }

    public void setParamPackagePath(String paramPackagePath) {
        this.paramPackagePath = paramPackagePath;
    }

    public String getParamRootPath() {
        return paramRootPath;
    }

    public void setParamRootPath(String paramRootPath) {
        this.paramRootPath = paramRootPath;
    }
}
