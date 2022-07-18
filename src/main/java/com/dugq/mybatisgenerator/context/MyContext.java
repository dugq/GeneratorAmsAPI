package com.dugq.mybatisgenerator.context;

import com.dugq.mybatisgenerator.config.MyAppendTableConfiguration;
import com.dugq.pojo.enums.MapperOpEnums;
import com.dugq.pojo.mybatis.AppendMapperConfigBean;
import com.dugq.pojo.mybatis.MySqlConfigBean;
import com.dugq.pojo.mybatis.TableConfigBean;
import com.intellij.openapi.project.Project;
import com.mysql.cj.conf.PropertyKey;
import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dugq
 * @date 2022/7/6 9:45 下午
 */
public class MyContext extends Context {

    private List<PluginConfiguration> pluginConfigurations;

    private AppendMapperConfigBean appendConfigBean;
    private TableConfigBean tableConfigBean;
    private MySqlConfigBean baseConfigBean;
    private Project project;
    private boolean appendGenerateParam;
    private boolean appendGenerateDto;
    private boolean appendGenerateEntity;
    private FullyQualifiedJavaType appendParamType;
    private FullyQualifiedJavaType appendDtoType;
    private FullyQualifiedJavaType appendEntityType;
    /**
     * Constructs a Context object.
     *
     * @param defaultModelType - may be null
     * @param project
     */
    public MyContext(ModelType defaultModelType, Project project) {
        super(defaultModelType);
        this.project = project;
        pluginConfigurations = new ArrayList<>();
    }


    public void setBaseConfig(MySqlConfigBean mySqlConfigBean) {
        this.baseConfigBean = mySqlConfigBean;
    }

    public AppendMapperConfigBean getAppendConfigBean() {
        return appendConfigBean;
    }

    public TableConfigBean getTableConfigBean() {
        return tableConfigBean;
    }

    public void setTableConfigBean(TableConfigBean tableConfigBean) {
        this.tableConfigBean = tableConfigBean;
    }

    public MySqlConfigBean getBaseConfigBean() {
        return baseConfigBean;
    }

    public Project getProject() {
        return project;
    }

    public void setAppendConfig(AppendMapperConfigBean appendConfig) {
        this.appendConfigBean = appendConfig;
    }

    public void ready() {
           appendGenerateParam = StringUtils.isNotBlank(appendConfigBean.getGenerateParamName());
           appendGenerateDto =  StringUtils.isNotBlank(appendConfigBean.getGenerateDtoName());
           appendGenerateEntity = StringUtils.isNotBlank(appendConfigBean.getGenerateEntityName());
           if (appendGenerateParam){
               String targetPackage = baseConfigBean.getParamPackagePath();
               if (genSubPackage()){
                   targetPackage = targetPackage +  "." + tableConfigBean.getSubPackage();
               }
               targetPackage = targetPackage + "." + appendConfigBean.getGenerateParamName();
               appendParamType = new FullyQualifiedJavaType(targetPackage);
           }
           if (appendGenerateDto){
               String targetPackage = baseConfigBean.getDtoPackagePath();
               if (genSubPackage()){
                   targetPackage = targetPackage +  "." + tableConfigBean.getSubPackage();
               }
               targetPackage = targetPackage + "." + appendConfigBean.getGenerateDtoName();
               appendDtoType = new FullyQualifiedJavaType(targetPackage);
           }
           if (appendGenerateEntity){
               String targetPackage = baseConfigBean.getEntityPackagePath();
               if (genSubPackage()){
                   targetPackage = targetPackage +  "." + tableConfigBean.getSubPackage();
               }
               targetPackage = targetPackage + "." + appendConfigBean.getGenerateEntityName();
               appendEntityType = new FullyQualifiedJavaType(targetPackage);
           }
    }

    public boolean isAppendGenerateParam() {
        return appendGenerateParam;
    }

    public boolean isAppendGenerateDto() {
        return appendGenerateDto;
    }

    public boolean isAppendGenerateEntity() {
        return appendGenerateEntity;
    }

    public FullyQualifiedJavaType getAppendParamType() {
        return appendParamType;
    }

    public FullyQualifiedJavaType getAppendDtoType() {
        return appendDtoType;
    }

    public FullyQualifiedJavaType getAppendEntityType() {
        return appendEntityType;
    }

    public boolean isListReturn(){
        return appendConfigBean.getOpEnums() == MapperOpEnums.BATCH_SELECT.getType();
    }

    public boolean isListParam(){
        return appendConfigBean.getOpEnums() == MapperOpEnums.BATCH_INSERT.getType();
    }

    public boolean isSelectModel() {
        return appendConfigBean.getOpEnums() == MapperOpEnums.BATCH_SELECT.getType() || appendConfigBean.getOpEnums() == MapperOpEnums.SELECT.getType();
    }

    public boolean isUpdateModel() {
        return appendConfigBean.getOpEnums() == MapperOpEnums.UPDATE.getType();
    }

    public boolean isInsertModel() {
        return appendConfigBean.getOpEnums() == MapperOpEnums.INSERT.getType() || appendConfigBean.getOpEnums() == MapperOpEnums.BATCH_INSERT.getType();
    }

    public boolean isDeleteModel() {
        return appendConfigBean.getOpEnums() == MapperOpEnums.DELETE.getType();
    }

    public void addPluginConfiguration(
            PluginConfiguration pluginConfiguration) {
        pluginConfigurations.add(pluginConfiguration);
        super.addPluginConfiguration(pluginConfiguration);
    }

    public boolean genSubPackage() {
        return StringUtils.isNotBlank(tableConfigBean.getSubPackage());
    }

    public void initSubPackage(){
        final String modelTargetPackage = getJavaModelGeneratorConfiguration().getTargetPackage();
        if (StringUtils.isNotBlank(modelTargetPackage)){
            getJavaModelGeneratorConfiguration().setTargetPackage(modelTargetPackage+"."+tableConfigBean.getSubPackage());
        }
        final String clientTargetPackage = getJavaClientGeneratorConfiguration().getTargetPackage();
        if (StringUtils.isNotBlank(clientTargetPackage)){
            getJavaClientGeneratorConfiguration().setTargetPackage(clientTargetPackage+"."+tableConfigBean.getSubPackage());
        }

        final String mapperTargetPackage = getSqlMapGeneratorConfiguration().getTargetPackage();
        if (StringUtils.isNotBlank(mapperTargetPackage)){
            getSqlMapGeneratorConfiguration().setTargetPackage(mapperTargetPackage+"."+tableConfigBean.getSubPackage());
        }

    }


    public static MyContext getAppendContextAndInitBaseConfig(MySqlConfigBean mySqlConfigBean, Project project) {
        String projectDir = project.getBasePath();
        MyContext mysqlContext = new MyContext(ModelType.CONDITIONAL,project);
        mysqlContext.setBaseConfig(mySqlConfigBean);
        mysqlContext.setId("mysql");
        mysqlContext.setTargetRuntime("org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl");
        mysqlContext.addProperty("autoDelimitKeywords","false");
        mysqlContext.addProperty("javaFileEncoding","UTF-8");
        mysqlContext.addProperty("beginningDelimiter","`");
        mysqlContext.addProperty("endingDelimiter","`");

        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.RowBoundsPlugin");
        mysqlContext.addPluginConfiguration(pluginConfiguration);

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setDriverClass("com.mysql.cj.jdbc.Driver");
        jdbcConnectionConfiguration.setConnectionURL(mySqlConfigBean.getDbUrl());
        jdbcConnectionConfiguration.setUserId(mySqlConfigBean.getDbUserName());
        jdbcConnectionConfiguration.setPassword(mySqlConfigBean.getDbPwd());
        jdbcConnectionConfiguration.addProperty("useInformationSchema","true");
        jdbcConnectionConfiguration.addProperty(PropertyKey.connectTimeout.getKeyName(),"3000");
        jdbcConnectionConfiguration.addProperty(PropertyKey.socketTimeout.getKeyName(),"3000");
        mysqlContext.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(projectDir+mySqlConfigBean.getMapperRootPath());
        sqlMapGeneratorConfiguration.setTargetPackage(mySqlConfigBean.getMapperPackagePath());
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages","true");
        mysqlContext.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration =  new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.setTargetProject(projectDir+mySqlConfigBean.getDaoRootPath());
        javaClientGeneratorConfiguration.setTargetPackage(mySqlConfigBean.getDaoPackagePath());
        javaClientGeneratorConfiguration.addProperty("enableSubPackages","true");
        mysqlContext.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.setConfigurationType("org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl");
        javaTypeResolverConfiguration.addProperty("forceBigDecimals","false");
        mysqlContext.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetProject(projectDir+mySqlConfigBean.getEntityRootPath());
        javaModelGeneratorConfiguration.setTargetPackage(mySqlConfigBean.getEntityPackagePath());
        javaModelGeneratorConfiguration.addProperty("constructorBased","false");
        javaModelGeneratorConfiguration.addProperty("enableSubPackages","true");
        javaModelGeneratorConfiguration.addProperty("trimStrings","true");
        mysqlContext.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        PluginConfiguration pluginConfiguration2 = new PluginConfiguration();
        pluginConfiguration2.setConfigurationType("com.dugq.mybatisgenerator.plugin.AppendPlugin");
        mysqlContext.addPluginConfiguration(pluginConfiguration2);
        return mysqlContext;
    }

    public void initTableConfig(TableConfigBean config){
        this.tableConfigBean = config;
        String tableName = config.getTableName();
        String domainName = config.getDomain();
        String packageName = config.getSubPackage();
        TableConfiguration tableConfiguration = new MyAppendTableConfiguration(this);
        tableConfiguration.setSelectByExampleStatementEnabled(false);
        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setWildcardEscapingEnabled(false);
        tableConfiguration.setInsertStatementEnabled(false);
        tableConfiguration.setSelectByPrimaryKeyStatementEnabled(false);
        tableConfiguration.setUpdateByPrimaryKeyStatementEnabled(false);
        tableConfiguration.setDeleteByPrimaryKeyStatementEnabled(false);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setUpdateByExampleStatementEnabled(false);
        tableConfiguration.setTableName(tableName);
        tableConfiguration.setDomainObjectName(domainName);
        //追加子包
        if (genSubPackage()){
            initSubPackage();
            tableConfiguration.addProperty("subPackage",packageName);
        }
        addTableConfiguration(tableConfiguration);
    }
}
