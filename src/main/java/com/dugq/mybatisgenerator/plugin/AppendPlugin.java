package com.dugq.mybatisgenerator.plugin;

import com.dugq.exception.SqlException;
import com.dugq.mybatisgenerator.commontGenerator.MyCommentGenerator;
import com.dugq.mybatisgenerator.context.MyContext;
import com.dugq.pojo.enums.MapperOpEnums;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2022/7/6 3:13 下午
 */
public class AppendPlugin extends PluginAdapter {

    final MyCommentGenerator myCommentGenerator = new MyCommentGenerator();



    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document,IntrospectedTable introspectedTable) {
        XmlElement element = null;
        MyContext myContext = (MyContext)context;
        final Map<String, IntrospectedColumn> columnMap = introspectedTable.getAllColumns().stream().collect(Collectors.toMap(IntrospectedColumn::getActualColumnName, Function.identity()));

        if (myContext.getAppendConfigBean().getOpEnums()== MapperOpEnums.SELECT.getType()){
            element = buildSelectSql(myContext, columnMap);
        }
        if (myContext.getAppendConfigBean().getOpEnums()== MapperOpEnums.UPDATE.getType()){
            element = buildUpdateSql(myContext, columnMap);
        }
        if (myContext.getAppendConfigBean().getOpEnums()== MapperOpEnums.DELETE.getType()){
            element = buildDeleteSql(myContext,columnMap);
        }
        if (myContext.getAppendConfigBean().getOpEnums()== MapperOpEnums.INSERT.getType()){
            element =buildInsertSql(myContext,columnMap);
        }
        if (myContext.getAppendConfigBean().getOpEnums()== MapperOpEnums.BATCH_INSERT.getType()){
            element =buildBatchInsertSql(myContext,columnMap);
        }
        if (myContext.getAppendConfigBean().getOpEnums()== MapperOpEnums.BATCH_SELECT.getType()){
            element = buildSelectSql(myContext, columnMap);
        }
        document.getRootElement().addElement(element);
        return true;
    }

    @NotNull
    public XmlElement buildSelectSql(MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        XmlElement element;
        element =  new XmlElement(MapperOpEnums.SELECT.getDesc());
        String returnType = myContext.getAppendConfigBean().getGenerateEntityName();
        if (StringUtils.isNotBlank(returnType)){
            element.addAttribute(new Attribute("resultType",returnType));
        }else{
            final List<String> selectColumns = myContext.getAppendConfigBean().getSelectColumns();
            if (selectColumns.size()==1){
                final String selectColumn = selectColumns.get(0);
                IntrospectedColumn introspectedColumn = columnMap.get(selectColumn);
                if (Objects.isNull(introspectedColumn)) {
                    throw new SqlException("filed not exist! name = " + selectColumn);
                }
                element.addAttribute(new Attribute("resultType",introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName()));
            }else{
                element.addAttribute(new Attribute("resultMap","BaseResultMap"));
            }
        }
        element.addAttribute(new Attribute("id", myContext.getAppendConfigBean().getMethodName()));
        //select columns from table
        final String columns = StringUtils.join(myContext.getAppendConfigBean().getSelectColumns(), ",");
        String sql = "select "+ columns + " from " + myContext.getTableConfigBean().getTableName();
        TextElement sqlElement = new TextElement(sql);
        element.addElement(sqlElement);
        XmlElement where = buildWhereElement(myContext, columnMap);
        element.addElement(where);
        return element;
    }

    public XmlElement buildInsertSql(MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        XmlElement element;
        element =  new XmlElement(MapperOpEnums.INSERT.getDesc());
        element.addAttribute(new Attribute("id", myContext.getAppendConfigBean().getMethodName()));
        //insert columns from table
        final String columns = StringUtils.join(myContext.getAppendConfigBean().getInsertColumns(), ",");
        //insert into tableName
        StringBuilder sql = new StringBuilder("insert into " + myContext.getTableConfigBean().getTableName());
        // (user_id,live_user_id,open_id,access_token,ext_type,oa_id)
        sql.append("\n(").append(columns).append(")\n");
        sql.append("\n values \n");
        TextElement sqlElement = new TextElement(sql.toString());
        element.addElement(sqlElement);
        element.addElement(buildTrimElement(columnMap,myContext.getAppendConfigBean().getInsertColumns(),null));
        return element;
    }

    public XmlElement buildBatchInsertSql(MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        XmlElement element =  new XmlElement(MapperOpEnums.INSERT.getDesc());
        element.addAttribute(new Attribute("id", myContext.getAppendConfigBean().getMethodName()));
        //insert columns from table
        final String columns = StringUtils.join(myContext.getAppendConfigBean().getInsertColumns(), ",");
        //insert into tableName
        StringBuilder sql = new StringBuilder("insert into " + myContext.getTableConfigBean().getTableName());
        // (user_id,live_user_id,open_id,access_token,ext_type,oa_id)
        sql.append("\n\t\t(").append(columns).append(")\n");
        sql.append("\t\tvalues");
        TextElement sqlElement = new TextElement(sql.toString());
        element.addElement(sqlElement);

        //<foreach collection="list" item="item" separator=",">
        final XmlElement foreachElement =  new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection","list"));
        foreachElement.addAttribute(new Attribute("item","item"));
        foreachElement.addAttribute(new Attribute("separator",","));
        final XmlElement trimElement = buildTrimElement(columnMap, myContext.getAppendConfigBean().getInsertColumns(),"item");
        foreachElement.addElement(trimElement);
        element.addElement(foreachElement);
        return element;
    }

    /**
     *  <trim prefix="(" suffix=")" suffixOverrides=",">
     *                 #{user.userId,jdbcType=BIGINT},
     *                 #{user.liveUserId,jdbcType=BIGINT},
     *                 #{user.openId,jdbcType=VARCHAR},
     *                 #{user.accessToken,jdbcType=VARCHAR},
     *                 #{user.extType,jdbcType=VARCHAR},
     *                 #{user.oaId},
     *     </trim>
     */
    public XmlElement buildTrimElement(Map<String, IntrospectedColumn> columnMap, List<String> columns,String prefix) {
        if(StringUtils.isBlank(prefix)){
            prefix= "";
        }else{
            prefix = prefix+".";
        }
        XmlElement trimElement =  new XmlElement("trim");
        trimElement.addAttribute(new Attribute("prefix","("));
        trimElement.addAttribute(new Attribute("suffixOverrides",","));
        trimElement.addAttribute(new Attribute("suffix",")"));
        for (String insertColumn :columns) {
            IntrospectedColumn introspectedColumn = columnMap.get(insertColumn);
            if (Objects.isNull(introspectedColumn)) {
                throw new SqlException("filed not exist! name = " + insertColumn);
            }
            final String property = introspectedColumn.getJavaProperty();
            String value = "#{" + prefix+property + ",jdbcType = " + introspectedColumn.getJdbcTypeName() + "},";
            trimElement.addElement(new TextElement(value));
        }
        return trimElement;
    }

    @NotNull
    public XmlElement buildDeleteSql(MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        XmlElement element;
        element =  new XmlElement(MapperOpEnums.DELETE.getDesc());
        element.addAttribute(new Attribute("id", myContext.getAppendConfigBean().getMethodName()));
        //delete from table
        String sql = "delete from " + myContext.getTableConfigBean().getTableName();
        TextElement sqlElement = new TextElement(sql);
        element.addElement(sqlElement);
        XmlElement where = buildWhereElement(myContext, columnMap);
        element.addElement(where);
        return element;
    }

    /**
     *             <where>
     *                  <if test="beginTime != null">
     *                     begin_time = #{beginTime},
     *                  </if>
     *             </where>
     * @return
     */
    public XmlElement buildWhereElement(MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        XmlElement where = new XmlElement("where");
        for (String whereColumn : myContext.getAppendConfigBean().getWhereColumns()) {
            final IntrospectedColumn introspectedColumn = columnMap.get(whereColumn);
            if (Objects.isNull(introspectedColumn)) {
                throw new SqlException("filed not exist! name = " + whereColumn);
            }
            final String property = introspectedColumn.getJavaProperty();
            XmlElement ifElement = new XmlElement("if");
            ifElement.addAttribute(new Attribute("test", property + "!=null"));
            where.addElement(ifElement);
            TextElement ifSql = new TextElement(whereColumn + "= #{" + property + ",jdbcType = " + introspectedColumn.getJdbcTypeName() + "},");
            where.addElement(ifSql);
        }
        return where;
    }

    @NotNull
    public XmlElement buildUpdateSql(MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        XmlElement element;
        element =  new XmlElement(MapperOpEnums.UPDATE.getDesc());
        element.addAttribute(new Attribute("id", myContext.getAppendConfigBean().getMethodName()));
        //update table
        String sql = "update " + myContext.getTableConfigBean().getTableName();
        TextElement sqlElement = new TextElement(sql);
        element.addElement(sqlElement);
//             <if test="beginTime != null">
//                  set  begin_time = #{beginTime},
//            </if>
        XmlElement set = new XmlElement("set");
        for (String updateColumn : myContext.getAppendConfigBean().getUpdateColumns()) {
            final IntrospectedColumn introspectedColumn = columnMap.get(updateColumn);
            if (Objects.isNull(introspectedColumn)){
                throw new SqlException("filed not exist! name = "+updateColumn);
            }
            final String property = introspectedColumn.getJavaProperty();
            XmlElement ifElement = new XmlElement("if");
            ifElement.addAttribute(new Attribute("test",property+"!=null"));
            set.addElement(ifElement);
            TextElement ifSql = new TextElement("set "+updateColumn+"= #{"+property+",jdbcType = "+introspectedColumn.getJdbcTypeName()+"},");
            set.addElement(ifSql);
        }
        element.addElement(set);
        XmlElement where = buildWhereElement(myContext, columnMap);
        element.addElement(where);
        return element;
    }

    public String getFiledName(Map<String, IntrospectedColumn> columnMap, String whereColumn) {
        final IntrospectedColumn introspectedColumn = columnMap.get(whereColumn);
        return introspectedColumn.getJavaProperty();
    }

    public Field getField(IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable){
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String  property = introspectedColumn.getJavaProperty();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fqjt);
        field.setName(property);
        myCommentGenerator.addFieldComment(field,introspectedTable, introspectedColumn);
        return field;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        MyContext myContext = (MyContext)context;
        final Map<String, IntrospectedColumn> columnMap = introspectedTable.getAllColumns().stream().collect(Collectors.toMap(IntrospectedColumn::getActualColumnName, Function.identity()));
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(myContext.getAppendConfigBean().getMethodName());
        //param处理
        if (myContext.isAppendGenerateParam()){
            final FullyQualifiedJavaType paramType = myContext.getAppendParamType();
            if (myContext.isListParam()){
                final FullyQualifiedJavaType newListInstance = FullyQualifiedJavaType.getNewListInstance();
                newListInstance.addTypeArgument(paramType);
                final Parameter parameter = new Parameter(newListInstance, myContext.getAppendConfigBean().getGenerateParamName()+"List");
                method.addParameter(parameter);
                interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
            }else{
                method.addParameter(new Parameter(paramType, myContext.getAppendConfigBean().getGenerateParamName()));
            }
            interfaze.addImportedType(paramType);
        }else{
            List<String> paramColumns = getParamFields(myContext);
            if (myContext.isListParam() || paramColumns.size()>3){
                final FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()+"Entity");
                final FullyQualifiedJavaType newListInstance = FullyQualifiedJavaType.getNewListInstance();
                newListInstance.addTypeArgument(paramType);
                final Parameter parameter = new Parameter(newListInstance, "list");
                method.addParameter(parameter);
                interfaze.addImportedType(paramType);
                interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
            }else{
                for (String selectColumn : paramColumns) {
                    final Field field = getField(columnMap.get(selectColumn), introspectedTable);
                    final FullyQualifiedJavaType paramType = field.getType();
                    final Parameter parameter = new Parameter(paramType, field.getName());
                    parameter.addAnnotation("@Param(\""+field.getName()+"\")");
                    interfaze.addImportedType(field.getType());
                    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
                    method.addParameter(parameter);
                }
            }
        }
      // return type 处理
        FullyQualifiedJavaType returnType;
        if (myContext.isAppendGenerateEntity()){
            returnType = myContext.getAppendEntityType();
        }else if (myContext.isSelectModel()){
            final List<String> selectColumns = myContext.getAppendConfigBean().getSelectColumns();
            if (selectColumns.size()==1){
                final String columnName = selectColumns.get(0);
                returnType = columnMap.get(columnName).getFullyQualifiedJavaType();
            }else{
                returnType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
            }
        }else{
            returnType = FullyQualifiedJavaType.getIntInstance();
        }
        setReturnType2Method(interfaze, myContext, method, returnType);
        interfaze.addMethod(method);
        method.addJavaDocLine("/**\n*"+myContext.getAppendConfigBean().getDesc()+"\n*/");
        return true;
    }

    private void setReturnType2Method(Interface interfaze, MyContext myContext, Method method, FullyQualifiedJavaType returnType) {
        if (myContext.isListReturn()) {
            FullyQualifiedJavaType listReturnType = FullyQualifiedJavaType.getNewListInstance();
            interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
            interfaze.addImportedType(returnType);
            listReturnType.addTypeArgument(returnType);
            method.setReturnType(listReturnType);
        } else {
            interfaze.addImportedType(returnType);
            method.setReturnType(returnType);
        }
    }


    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        MyContext myContext = (MyContext)context;
        final Map<String, IntrospectedColumn> columnMap = introspectedTable.getAllColumns().stream().collect(Collectors.toMap(IntrospectedColumn::getActualColumnName, Function.identity()));

        List<GeneratedJavaFile> javaFiles = new ArrayList<>();
        if (StringUtils.isNotBlank(myContext.getAppendConfigBean().getGenerateDtoName())){
            javaFiles.add(buildDto(introspectedTable,myContext,columnMap));
        }
        if (StringUtils.isNotBlank(myContext.getAppendConfigBean().getGenerateParamName())){
            javaFiles.add(buildParam(introspectedTable,myContext,columnMap));
        }
        if (StringUtils.isNotBlank(myContext.getAppendConfigBean().getGenerateEntityName())){
            javaFiles.add(buildEntity(introspectedTable,myContext,columnMap));
        }
        return javaFiles;
    }

    /**
     * 构建返回值对象，只正对于select
     */
    private GeneratedJavaFile buildEntity(IntrospectedTable introspectedTable, MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        if (!myContext.isAppendGenerateEntity()|| !myContext.isSelectModel()){
            return null;
        }
        TopLevelClass topLevelClass = new TopLevelClass(myContext.getAppendEntityType());
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        for (String selectColumn : myContext.getAppendConfigBean().getSelectColumns()) {
            final IntrospectedColumn introspectedColumn = columnMap.get(selectColumn);
            topLevelClass.addField(getField(introspectedColumn,introspectedTable));
        }
        return new GeneratedJavaFile(topLevelClass,myContext.getProject().getBasePath()+myContext.getBaseConfigBean().getEntityRootPath(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
    }

    /**
     * 自动构建入参数
     * insert 根据插入字段列表生成
     * update 根据update字段和where字段列表生成
     * delete 根据where字段列表
     * select 根据where字段列表
     */
    private GeneratedJavaFile buildParam(IntrospectedTable introspectedTable, MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        if (!myContext.isAppendGenerateParam()){
            return null;
        }
        TopLevelClass topLevelClass = new TopLevelClass(myContext.getAppendParamType());
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addImportedType("lombok.Data");
        List<String> paramColumns = getParamFields(myContext);

        for (String column : paramColumns) {
            final IntrospectedColumn introspectedColumn = columnMap.get(column);
            topLevelClass.addField(getField(introspectedColumn,introspectedTable));
        }
        return new GeneratedJavaFile(topLevelClass,myContext.getProject().getBasePath()+myContext.getBaseConfigBean().getParamRootPath(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
    }

    @NotNull
    private List<String> getParamFields(MyContext myContext) {
        List<String> paramColumns =  new ArrayList<>();
        if (myContext.isInsertModel()){
            paramColumns.addAll(myContext.getAppendConfigBean().getInsertColumns());
        }else if (myContext.isUpdateModel()){
            paramColumns.addAll(myContext.getAppendConfigBean().getUpdateColumns());
            paramColumns.addAll(myContext.getAppendConfigBean().getWhereColumns());
        }else{
            paramColumns.addAll(myContext.getAppendConfigBean().getWhereColumns());
        }
        return paramColumns;
    }

    /**
     * 自动构建入Dto其实就是entity的序列化对象
     * 只针对于select
     */
    private GeneratedJavaFile buildDto(IntrospectedTable introspectedTable, MyContext myContext, Map<String, IntrospectedColumn> columnMap) {
        if (!myContext.isAppendGenerateDto() || !myContext.isSelectModel()){
            return null;
        }
        TopLevelClass topLevelClass = new TopLevelClass(myContext.getAppendDtoType());
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addImportedType("lombok.Data");
        Field ser = new Field();
        ser.setName("serialVersionUID");
        ser.setInitializationString(System.currentTimeMillis()+""+ RandomUtils.nextLong(0,10000) +"L");
        ser.setType(new FullyQualifiedJavaType("long"));
        ser.setStatic(true);
        ser.setFinal(true);
        ser.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(ser);
        final FullyQualifiedJavaType superInterface = new FullyQualifiedJavaType("java.io.Serializable");
        topLevelClass.addSuperInterface(superInterface);
        topLevelClass.addImportedType(superInterface);
        for (String selectColumn : myContext.getAppendConfigBean().getSelectColumns()) {
            final IntrospectedColumn introspectedColumn = columnMap.get(selectColumn);
            topLevelClass.addField(getField(introspectedColumn,introspectedTable));
        }
        return new GeneratedJavaFile(topLevelClass,myContext.getProject().getBasePath()+myContext.getBaseConfigBean().getDtoRootPath(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        return super.contextGenerateAdditionalXmlFiles(introspectedTable);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //禁止model的生成
        return false;
    }

}
