package com.dugq.mybatisgenerator.generator3;

import com.dugq.exception.SqlException;
import com.dugq.pojo.mybatis.MySqlConfigBean;
import com.dugq.pojo.mybatis.TableConfigBean;
import com.dugq.service.config.impl.MybatisConfigService;
import com.dugq.util.APIPrintUtil;
import com.intellij.openapi.project.Project;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2019-07-26.
 */
public class MyGenerator {
    private final Project project;
    Configuration configuration;
    private MybatisConfigService mybatisConfigService;

    public MyGenerator(Project project) {
        this.project = project;
        mybatisConfigService = project.getService(MybatisConfigService.class);
    }

    public void init(){
        final MySqlConfigBean mySqlConfigBean = mybatisConfigService.getAndFillIfEmpty();
        String projectDir = project.getBasePath();
        configuration = builderConfig(mySqlConfigBean,projectDir);
    }

    private Configuration builderConfig(MySqlConfigBean mySqlConfigBean, String dir) {
        Configuration configuration = new Configuration();
        Context mysqlContext = new Context(ModelType.CONDITIONAL);
        mysqlContext.setId("mysql");
        mysqlContext.setTargetRuntime("com.dugq.mybatisgenerator.generator3.MyIntrospectedTableMybatis3Impl");
        mysqlContext.addProperty("autoDelimitKeywords","false");
        mysqlContext.addProperty("javaFileEncoding","UTF-8");
        mysqlContext.addProperty("beginningDelimiter","`");
        mysqlContext.addProperty("endingDelimiter","`");
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.RowBoundsPlugin");
        mysqlContext.addPluginConfiguration(pluginConfiguration);

        PluginConfiguration pluginConfiguration1 = new PluginConfiguration();
        pluginConfiguration1.setConfigurationType("com.dugq.mybatisgenerator.plugin.MyPluginAdapter");
        pluginConfiguration1.addProperty("dtoPath",mySqlConfigBean.getDtoPackagePath());
        pluginConfiguration1.addProperty("paramPath",mySqlConfigBean.getParamPackagePath());
        pluginConfiguration1.addProperty("dtoTargetProject",dir+mySqlConfigBean.getDtoRootPath());
        pluginConfiguration1.addProperty("paramTargetProject",dir+mySqlConfigBean.getParamRootPath());
        mysqlContext.addPluginConfiguration(pluginConfiguration1);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType("com.dugq.mybatisgenerator.commontGenerator.MyCommentGenerator");
        commentGeneratorConfiguration.addProperty("suppressDate","true");
        commentGeneratorConfiguration.addProperty("suppressAllComments","true");
        mysqlContext.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setDriverClass("com.mysql.cj.jdbc.Driver");
        jdbcConnectionConfiguration.setConnectionURL(mySqlConfigBean.getDbUrl());
        jdbcConnectionConfiguration.setUserId(mySqlConfigBean.getDbUserName());
        jdbcConnectionConfiguration.setPassword(mySqlConfigBean.getDbPwd());
        jdbcConnectionConfiguration.addProperty("useInformationSchema","true");
        jdbcConnectionConfiguration.addProperty(PropertyKey.connectTimeout.getKeyName(),"3000");
        jdbcConnectionConfiguration.addProperty(PropertyKey.socketTimeout.getKeyName(),"3000");
        mysqlContext.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.setConfigurationType("org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl");
        javaTypeResolverConfiguration.addProperty("forceBigDecimals","false");
        mysqlContext.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetProject(dir+mySqlConfigBean.getEntityRootPath());
        javaModelGeneratorConfiguration.setTargetPackage(mySqlConfigBean.getEntityPackagePath());
        javaModelGeneratorConfiguration.addProperty("constructorBased","false");
        javaModelGeneratorConfiguration.addProperty("enableSubPackages","true");
        javaModelGeneratorConfiguration.addProperty("trimStrings","true");
        mysqlContext.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(dir+mySqlConfigBean.getMapperRootPath());
        sqlMapGeneratorConfiguration.setTargetPackage(mySqlConfigBean.getMapperPackagePath());
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages","true");
        mysqlContext.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration =  new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.setTargetProject(dir+mySqlConfigBean.getDaoRootPath());
        javaClientGeneratorConfiguration.setTargetPackage(mySqlConfigBean.getDaoPackagePath());
        javaClientGeneratorConfiguration.addProperty("enableSubPackages","true");
        mysqlContext.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        configuration.addContext(mysqlContext);
        return configuration;
    }

    public MyGenerator addTable(String tableName,String domainName,String packageName,boolean genDto,boolean genParam){
        Context mysql = configuration.getContext("mysql");
        Context context = new Context(ModelType.CONDITIONAL);
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setSelectByExampleStatementEnabled(false);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setUpdateByExampleStatementEnabled(false);
        tableConfiguration.setWildcardEscapingEnabled(false);
        tableConfiguration.setTableName(tableName);
        tableConfiguration.setDomainObjectName(domainName);

        tableConfiguration.addProperty("genDto",String.valueOf(genDto));
        tableConfiguration.addProperty("genParam",String.valueOf(genParam));


        tableConfiguration.addProperty("subPackage",packageName);
        mysql.addTableConfiguration(tableConfiguration);
        return this;
    }



    public void generator() {
        Context mysql = configuration.getContext("mysql");
        List<TableConfiguration> tableConfigurations = mysql.getTableConfigurations();
        if(CollectionUtils.isEmpty(tableConfigurations)){
            throw new RuntimeException("调用 addTable 方法 添加表结构");
        }
        try {
            DefaultShellCallback callback = new DefaultShellCallback(true);
            ArrayList<String> warnings = new ArrayList<>();
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);
            myBatisGenerator.generate(null);
            if(CollectionUtils.isNotEmpty(warnings)){
                APIPrintUtil.clear(project);
                warnings.forEach(warning-> APIPrintUtil.printErrorLine(warning,project));
            }
        } catch (CommunicationsException exception){
            throw new SqlException("can not connect to db!");
        }catch (Exception e) {
            APIPrintUtil.clear(project);
            APIPrintUtil.printException(e,project);
            e.printStackTrace();
        }
    }

    public void addTable(TableConfigBean config) {
        addTable(config.getTableName(),config.getDomain(),config.getSubPackage(),config.isGenerateDto(),config.isGenerateParam());
    }
}
