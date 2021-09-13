package org.xht.xdb.enums;

/**
 * 支持的数据库类型
 */
public enum DbType {
    ORACLE,
    SQLITE,
    DAMENG,
    SQLSERVER,
    MYSQL,
    PostgreSQL,
    KINGBASE,
    H2;

    public static DbType of(String driverClassName) {
        switch (driverClassName) {
            case "oracle.jdbc.OracleDriver":
            case "oracle.jdbc.driver.OracleDriver":
                return DbType.ORACLE;
            case "dm.jdbc.driver.DmDriver":
                return DbType.DAMENG;
            case "com.microsoft.sqlserver.jdbc.SQLServerDriver":
                return DbType.SQLSERVER;
            case "com.mysql.jdbc.Driver":
            case "com.mysql.cj.jdbc.Driver":
                return DbType.MYSQL;
            case "org.postgresql.Driver":
                return DbType.PostgreSQL;
            case "com.kingbase.Driver":
                return DbType.KINGBASE;
            case "org.h2.Driver":
                return DbType.H2;
            default:
                return DbType.SQLITE;
        }
    }

    //优先查找.sql.oracle和.sql.sqlite后缀的sql文件，找不到时才使用.sql后缀的sql文件
    public static String getSqlFileSuffix(String sqlFileName, DbType dbType) {
        switch (dbType) {
            case ORACLE:
                return sqlFileName + ".oracle";
            case DAMENG:
                return sqlFileName + ".dameng";
            case SQLSERVER:
                return sqlFileName + ".sqlserver";
            case MYSQL:
                return sqlFileName + ".mysql";
            case PostgreSQL:
                return sqlFileName + ".postgresql";
            case KINGBASE:
                return sqlFileName + ".kingbase";
            case H2:
                return sqlFileName + ".h2";
            default:
                return sqlFileName + ".sqlite";
        }
    }

    public static String getLimitSql(String sql, Long limitFrom, Long limitTo, DbType dbType) {
        if (DbType.ORACLE.equals(dbType)) {
            sql = String.format("select t.* from (select x.*,rownum rn from ( %s ) x) t where rn > %s and rn < %s", sql, limitFrom - 1, limitTo + 1);
        } else {
            sql = String.format("select t.* from ( %s ) t limit %s , %s", sql, limitFrom - 1, limitTo - limitFrom + 1);
        }
        return sql;
    }
}
