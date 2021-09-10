package xht.xdb.sql;

import xht.xdb.enums.DbType;
import xht.xdb.sql.statement.ConnTool;
import xht.xdb.util.MapUtil;
import xht.xdb.util.SqlFileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * sql执行工具
 */
@SuppressWarnings("ALL")
@Slf4j
public class SqlTool {
    @Getter
    private String sql;
    private DbType dbType;
    private MapUtil sqlArgs;
    private String sqlFileName;
    private String sqlFileRelativePath;
    private Class sqlFileClass;
    private Long pageIndex;
    private Long pagePerSize;
    private Long limitFrom;
    private Long limitTo;

    public Map<String, Object> getSqlArgs() {
        return sqlArgs.value();
    }

    public SqlTool(DbType dbType) {
        this.sqlArgs = MapUtil.init();
        this.dbType = dbType;
    }

    public SqlTool dbType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

    /**
     * <pre>
     *     执行查询语句，返回查询结果集合，若查询结果为空或执行失败时返回空的list实例对象
     * </pre>
     *
     * @param autoCloseConnection 自动关闭连接
     * @return ResultQuery
     */
    public ResultQuery executeQuery(boolean... autoCloseConnection) {
        getSqlFromFile();
        setArraySqlArgs();
        limitSql();
        return ConnTool.executeQuery(this.sql, this.sqlArgs, autoCloseConnection);
    }

    /**
     * <pre>
     *     获取count
     * </pre>
     *
     * @param autoCloseConnection 自动关闭连接
     * @return ResultQuery
     */
    public long executeCount(boolean... autoCloseConnection) {
        getSqlFromFile();
        setArraySqlArgs();
        limitSql();
        this.sql = String.format("select count(1) as count from ( %s )", this.sql);
        long count;
        ResultQuery resultQuery = ConnTool.executeQuery(this.sql, this.sqlArgs, autoCloseConnection);
        Object _count = resultQuery.first().get("COUNT");
        if (_count instanceof BigDecimal) {
            count = ((BigDecimal) _count).longValue();
        } else {
            count = (long) _count;
        }
        return count;
    }

    /**
     * <pre>
     * 执行非查询类sql语句(insert、delete、update和),返回结果有两种可能：
     * 1）dml类sql返回变更的记录个数
     * 2）返回0或者没有返回结果的sql执行
     * </pre>
     *
     * @param autoCloseConnection 自动关闭连接
     * @return {@link ResultUpdate}
     */
    public ResultUpdate executeUpdate(boolean... autoCloseConnection) {
        getSqlFromFile();
        setArraySqlArgs();
        limitSql();
        return ConnTool.execute(this.sql, this.sqlArgs, autoCloseConnection);
    }

    /**
     * 批量执行
     *
     * @param values              值
     * @param batchSize           批量大小
     * @param autoCommit          自动提交
     * @param autoCloseConnection 自动关闭连接
     * @return {@link ResultBatch}
     */
    public ResultBatch executeBatch(List<Object[]> values, int batchSize, boolean autoCommit, boolean... autoCloseConnection) {
        return ConnTool.executeBatch(this.sql, values, 1000, true, autoCloseConnection);
    }

    /**
     * 获取执行前格式化完毕的sql语句
     *
     * @return {@link SqlTool}
     */
    public SqlTool debug() {
        format();
        log.debug(this.sql);
        log.debug("{}", this.sqlArgs);
        return this;
    }

    /**
     * 格式sql,args,page
     *
     * @return {@link SqlTool}
     */
    public SqlTool format() {
        getSqlFromFile();
        setArraySqlArgs();
        limitSql();
        return this;
    }

    /**
     * 结果集限制：开始位置(结果集包含此位置)
     *
     * @param limitFrom 开始位置(结果集包含此位置)
     * @return SqlTool
     */
    public SqlTool limitFrom(Long limitFrom) {
        this.limitFrom = limitFrom;
        return this;
    }

    /**
     * 结果集限制：结束位置(结果集包含此位置)
     *
     * @param limitTo 结束位置(结果集包含此位置)
     * @return SqlTool
     */
    public SqlTool limitTo(Object limitTo) {
        this.limitTo = (Long) limitTo;
        return this;
    }

    /**
     * 分页：当前第几页数据 ( 序号从1起始 )
     *
     * @param pageIndex 当前第几页 ( 序号从1起始 )
     * @return SqlTool
     */
    public SqlTool pageIndex(Object pageIndex) {
        this.pageIndex = pageIndex == null ? null : Long.parseLong(String.valueOf(pageIndex));
        return this;
    }

    /**
     * 分页：每页多少条数据
     *
     * @param pagePerSize 每页多少条数据
     * @return SqlTool
     */
    public SqlTool pagePerSize(Object pagePerSize) {
        this.pagePerSize = pagePerSize == null ? null : Long.parseLong(String.valueOf(pagePerSize));
        return this;
    }

    /**
     * <pre>
     * 1、sql 中参数以":"开始,如“:id”,":name"
     * 2、参数前后必须留有空格
     * </pre>
     *
     * @param sql sql
     * @return SqlTool
     */
    public SqlTool sql(String sql) {
        this.sql = sql;
        return this;
    }

    /**
     * sql执行参数
     *
     * @param sqlArgs sql执行参数
     * @return SqlTool
     */
    @SuppressWarnings("unused")
    public SqlTool sqlArgs(MapUtil sqlArgs) {
        this.sqlArgs = sqlArgs;
        return this;
    }

    /**
     * sql执行参数
     *
     * @param key    关键
     * @param sqlArg sql参数
     * @return SqlTool
     */
    @SuppressWarnings("unused")
    public SqlTool sqlArg(String key, Object sqlArg) {
        this.sqlArgs.add(key, sqlArg);
        return this;
    }

    /**
     * <pre>
     * 功能：动态组装sql
     *    1、sqlFileClass和sqlFileName需在同一目录层级
     *    2、sql文件中的参数使用冒号占位符，如" :id "," :name "
     *    3、冒号占位符前后必须留有空格
     *    4、注释语句以1行为单位，1行内的所有参数占位符如果都在sqlArgs中传入，则自动放开此行语句
     * </pre>
     *
     * @param sqlFileClass sqlFileClass和sqlFileName需在同一目录层级
     * @param sqlFileName  sqlFileName
     * @return SqlTool
     */
    public SqlTool sqlFile(Class sqlFileClass, String sqlFileName) {
        this.sqlFileClass = sqlFileClass;
        this.sqlFileName = sqlFileName;
        return this;
    }

    private SqlTool limitSql() {
        if (limitFrom == null && limitTo == null) {
            if (pageIndex != null && pagePerSize != null) {
                limitFrom = pagePerSize * (pageIndex - 1) + 1;
                limitTo = limitFrom + pagePerSize - 1;
            }
        }
        if (limitFrom != null && limitTo != null) {
            this.dbType = this.dbType == null ? DbType.SQLITE : this.dbType;
            this.sql = DbType.getLimitSql(this.sql, this.limitFrom, this.limitTo, this.dbType);
        }
        return this;
    }

    private void getSqlFromFile() {
        if (sql == null) {
            if (this.sqlFileRelativePath != null) {
                this.sql = SqlFileUtil.getSql(sqlFileRelativePath, sqlArgs);
            } else if (sqlFileClass != null && sqlFileName != null) {
                this.sql = SqlFileUtil.getSql(sqlFileClass, sqlFileName, sqlArgs);
            }
        }
    }

    /**
     * 由于各jdbc驱动对Connection.createArrayOf的支持不同，这里使用替换的方法实现数组类型参数的处理
     */
    private void setArraySqlArgs() {
        Set<String> keys = this.sqlArgs.keySet();
        int size = keys.size();
        String[] _keys = new String[size];
        _keys = keys.toArray(_keys);
        for (int i = 0; i < size; i++) {
            String key = _keys[i];
            Object value = sqlArgs.get(key);
            if (value != null) {
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    Collection collection = (Collection) value;
                    Object[] arr = collection.toArray();
                    arrayArgs2SingleArg(key, arr);
                } else if (Object[].class.isAssignableFrom(value.getClass())) {
                    Object[] arr = (Object[]) value;
                    arrayArgs2SingleArg(key, arr);
                }
            }
        }
    }

    private void arrayArgs2SingleArg(String key, Object[] values) {
        String _key = String.format("%s__%s", key, 0);
        String keyJoin = String.format(" :%s__0 ", key);
        this.sqlArgs.del(key).add(_key, values[0]);
        for (int i = 1, len = values.length; i < len; i++) {
            _key = String.format("%s__%s", key, i);
            keyJoin = String.format("%s, :%s ", keyJoin, _key);
            this.sqlArgs.add(_key, values[i]);
        }
        this.sql = this.sql.replaceAll(String.format(":%s ", key), keyJoin + " ");
        this.sql = this.sql.replaceAll(String.format(":%s\\)", key), keyJoin + ")");
        this.sql = this.sql.replaceAll(String.format(":%s\\,", key), keyJoin + ",");
    }

    public SqlTool sqlFile(String sqlFileRelativePath) {
        if (sqlFileRelativePath.endsWith(".sql")) {
            this.sqlFileRelativePath = sqlFileRelativePath;
        } else {
            this.sqlFileRelativePath = sqlFileRelativePath + ".sql";
        }
        return this;
    }

    public static void main(String[] args) {
//        String sql = "(id=:id,name=2)";
//        System.out.print(sql + "  -> ");
//        System.out.println(sql.replaceAll(":id\\,", "1,"));
//        sql = "id=:id    and name=2";
//        System.out.print(sql + "  -> ");
//        System.out.println(sql.replaceAll(":id\\ ", "1 "));
//        sql = "in (name=2,id=:id)";
//        System.out.print(sql + "  -> ");
//        System.out.println(sql.replaceAll(":id\\)", "1)"));

//        Object l = null;
//        Object l = "1";
//        Object l = 1l;
//        Object l = 1;
        Object l = 1.1;
        Long x = l == null ? null : Long.parseLong(String.valueOf(l));
        System.out.println(x);
    }
}
