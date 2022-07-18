package com.dugq.mybatisgenerator;

import org.mybatis.generator.api.JavaTypeResolver;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.db.DatabaseIntrospector;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static org.mybatis.generator.internal.util.StringUtility.stringContainsSpace;

/**
 * @author dugq
 * @date 2022/7/5 4:25 下午
 */
public class MyDatabaseIntrospector extends DatabaseIntrospector {
    private DatabaseMetaData myDatabaseMetaData;
    /**
     * Instantiates a new database introspector.
     *
     * @param context          the context
     * @param myDatabaseMetaData the database meta data
     * @param javaTypeResolver the java type resolver
     * @param warnings
     */
    public MyDatabaseIntrospector(Context context, DatabaseMetaData myDatabaseMetaData, JavaTypeResolver javaTypeResolver, List<String> warnings) {
        super(context, myDatabaseMetaData, javaTypeResolver, warnings);
        this.myDatabaseMetaData = myDatabaseMetaData;
    }

    public List<String> getIndexColumns(TableConfiguration tc) throws SQLException {
        String localCatalog;
        String localSchema;
        String localTableName;

        boolean delimitIdentifiers = tc.isDelimitIdentifiers()
                || stringContainsSpace(tc.getCatalog())
                || stringContainsSpace(tc.getSchema())
                || stringContainsSpace(tc.getTableName());

        if (delimitIdentifiers) {
            localCatalog = tc.getCatalog();
            localSchema = tc.getSchema();
            localTableName = tc.getTableName();
        } else if (myDatabaseMetaData.storesLowerCaseIdentifiers()) {
            localCatalog = tc.getCatalog() == null ? null : tc.getCatalog()
                    .toLowerCase();
            localSchema = tc.getSchema() == null ? null : tc.getSchema()
                    .toLowerCase();
            localTableName = tc.getTableName() == null ? null : tc
                    .getTableName().toLowerCase();
        } else if (myDatabaseMetaData.storesUpperCaseIdentifiers()) {
            localCatalog = tc.getCatalog() == null ? null : tc.getCatalog()
                    .toUpperCase();
            localSchema = tc.getSchema() == null ? null : tc.getSchema()
                    .toUpperCase();
            localTableName = tc.getTableName() == null ? null : tc
                    .getTableName().toUpperCase();
        } else {
            localCatalog = tc.getCatalog();
            localSchema = tc.getSchema();
            localTableName = tc.getTableName();
        }

        if (tc.isWildcardEscapingEnabled()) {
            String escapeString = myDatabaseMetaData.getSearchStringEscape();

            StringBuilder sb = new StringBuilder();
            StringTokenizer st;
            if (localSchema != null) {
                st = new StringTokenizer(localSchema, "_%", true); //$NON-NLS-1$
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (token.equals("_") //$NON-NLS-1$
                            || token.equals("%")) { //$NON-NLS-1$
                        sb.append(escapeString);
                    }
                    sb.append(token);
                }
                localSchema = sb.toString();
            }

            sb.setLength(0);
            st = new StringTokenizer(localTableName, "_%", true); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equals("_") //$NON-NLS-1$
                        || token.equals("%")) { //$NON-NLS-1$
                    sb.append(escapeString);
                }
                sb.append(token);
            }
            localTableName = sb.toString();
        }
        ResultSet rs2 =myDatabaseMetaData.getIndexInfo(localCatalog,localSchema,localTableName,false,false);
        List<String> list = new ArrayList<>();
        while(rs2.next()){
            list.add(rs2.getString("COLUMN_NAME"));
        }
        return list;
    }
}
