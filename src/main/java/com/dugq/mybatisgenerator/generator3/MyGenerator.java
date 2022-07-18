package cn.com.duiba.live.normal.service.mybatisgenerator.generator3;

import com.mysql.cj.jdbc.Driver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2019-07-26.
 */
@Slf4j
public class MyGenerator {
    Configuration configuration;
    private boolean generatorPackage;
    private boolean generatorDto;
    private boolean generatorParam = true;

    public MyGenerator() {
        List<String> warnings = new ArrayList<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        String dir = System.getProperty("user.dir");
        try {
            configuration = cp.parseConfiguration(is);
            Driver.class.getClassLoader();
            Context mysql = configuration.getContext("mysql");
            mysql.getSqlMapGeneratorConfiguration().setTargetProject(dir+"/live-normal-service-biz/src/main/resources");
            mysql.getJavaModelGeneratorConfiguration().setTargetProject(dir+"/live-normal-service-biz/src/main/java");
            mysql.getJavaClientGeneratorConfiguration().setTargetProject(dir+"/live-normal-service-biz/src/main/java");
            List<TableConfiguration> tableConfigurations = mysql.getTableConfigurations();
            tableConfigurations.clear();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置是生成子包 默认不生成。 设置为true，默认为类名前缀。可在添加table时自定义
     * @param generatorPackage
     * @return
     */
    public MyGenerator setGeneratorPackage(boolean generatorPackage) {
        this.generatorPackage = generatorPackage;
        return this;
    }

    /**
     * 设置是否生成DTO类。不默认生产
     * @param generatorDto
     * @return
     */
    public MyGenerator setGeneratorDto(boolean generatorDto) {
        this.generatorDto = generatorDto;
        return this;
    }

    /**
     * 设置是否生成Param类。不默认生产
     * @param generatorParam 是否生成dto类
     * @return 当前对象
     */
    public MyGenerator setGeneratorParam(boolean generatorParam) {
        this.generatorParam = generatorParam;
        return this;
    }

    /**
     *
     * @param tableName  表名
     * @param domainName  类名前缀
     */
    public MyGenerator addTable(String tableName,String domainName){
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
        if(generatorPackage){
            tableConfiguration.addProperty("subPackage",domainName.toLowerCase());
        }
        if(generatorDto){
            tableConfiguration.addProperty("genDto","true");
        }
        if (generatorParam) {
            tableConfiguration.addProperty("genParam","true");
        }
        mysql.addTableConfiguration(tableConfiguration);
        return this;
    }

    public MyGenerator addTable(String tableName,String domainName,String packageName){
        generatorPackage =  true;
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
        if(generatorDto){
            tableConfiguration.addProperty("genDto","true");
        }
        if (generatorParam) {
            tableConfiguration.addProperty("genParam","true");
        }
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
                warnings.forEach(log::error);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
