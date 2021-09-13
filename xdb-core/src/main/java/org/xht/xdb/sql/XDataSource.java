package org.xht.xdb.sql;

import org.xht.xdb.enums.DbType;

import javax.sql.DataSource;

/**
 * XDataSource是DataSource的封装类，添加了数据源名称（name）和数据源类型（dbType）
 *
 */
@SuppressWarnings("unused")
public class XDataSource {
    private DataSource dataSource;
    private String name;
    private DbType dbType;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public XDataSource(DataSource dataSource, DbType type, String name) {
        this.dataSource = dataSource;
        this.name = name;
        this.dbType = type;
    }
}
