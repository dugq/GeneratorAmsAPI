package com.dugq.mybatisgenerator.util;

import com.dugq.exception.SqlException;
import com.dugq.mybatisgenerator.MyDatabaseIntrospector;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.generator.api.ConnectionFactory;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaTypeResolver;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.JDBCConnectionFactory;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.db.DatabaseIntrospector;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.StringUtility.composeFullyQualifiedTableName;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author dugq
 * @date 2022/7/5 3:34 下午
 */
public class DbUtil {

    public static List<String> getAllColumns(Context context, TableConfiguration tc){
        final IntrospectedTable introspectedTable = introspectTables(context, tc);
        return introspectedTable.getAllColumns().stream().map(IntrospectedColumn::getActualColumnName).collect(Collectors.toList());
    }


    public static IntrospectedTable introspectTables(Context context, TableConfiguration tc){
        List<String> warnings = new ArrayList<>();
        final List<IntrospectedTable> introspectedTables;
        try {
            introspectedTables = introspectTables(context, tc, warnings);
        }catch (CommunicationsException exception){
          throw new SqlException("can not connect to db!");
        }catch (SQLException sqlException) {
            throw new SqlException(sqlException.getLocalizedMessage());
        }
        if (CollectionUtils.isNotEmpty(warnings)){
            throw new SqlException(warnings);
        }
        if (CollectionUtils.isEmpty(introspectedTables)){
            throw new SqlException("未找到表："+tc.getTableName());
        }
        if (introspectedTables.size()>1){
            throw new SqlException("表名【"+tc.getTableName()+"】重复。请确认数据库连接是否精确到库");
        }
        return introspectedTables.get(0);
    }

    private static List<IntrospectedTable> introspectTables(Context context, TableConfiguration tc,List<String> warnings)throws SQLException {
        Connection connection = null;
        try {
            JavaTypeResolver javaTypeResolver = ObjectFactory
                    .createJavaTypeResolver(context, warnings);
            connection = getConnection(context.getJdbcConnectionConfiguration());
            DatabaseIntrospector databaseIntrospector = new DatabaseIntrospector(
                    context, connection.getMetaData(), javaTypeResolver, warnings);
                    return databaseIntrospector.introspectTables(tc);
        } catch (CommunicationsException exception){
            throw new SqlException("can not connect to db!");
        }catch (SQLException sqlException) {
            throw new SqlException(sqlException.getLocalizedMessage());
        }finally {
            closeConnection(connection);
        }
    }


    public static List<String> getIndexColumn(Context context, TableConfiguration tc){
        List<String> warnings = new ArrayList<>();
        List<String> indexColumns;
        try {
            indexColumns = getIndexColumn(context, tc, warnings);
        } catch (SQLException sqlException) {
            throw new SqlException(sqlException.getLocalizedMessage());
        }
        if (CollectionUtils.isNotEmpty(warnings)){
            throw new SqlException(warnings);
        }
        return indexColumns;
    }

    public static List<String> getIndexColumn(Context context, TableConfiguration tc,List<String> warnings)throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection(context.getJdbcConnectionConfiguration());
            final DatabaseMetaData metaData = connection.getMetaData();
            JavaTypeResolver javaTypeResolver = ObjectFactory
                    .createJavaTypeResolver(context, warnings);
            MyDatabaseIntrospector databaseIntrospector = new MyDatabaseIntrospector(
                    context, metaData, javaTypeResolver, warnings);
            String tableName = composeFullyQualifiedTableName(tc.getCatalog(), tc
                    .getSchema(), tc.getTableName(), '.');
            if (!tc.areAnyStatementsEnabled()) {
                warnings.add(getString("Warning.0", tableName)); //$NON-NLS-1$
                return null;
            }else{
                return databaseIntrospector.getIndexColumns(tc);
            }
        } finally {
            closeConnection(connection);
        }
    }

    private static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private static Connection getConnection(JDBCConnectionConfiguration jdbcConnectionConfiguration) throws SQLException {
        ConnectionFactory connectionFactory;
        connectionFactory = new JDBCConnectionFactory(jdbcConnectionConfiguration);
        return connectionFactory.getConnection();
    }
}
