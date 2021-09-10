package xht.xdb;

import xht.xdb.enums.DbType;
import xht.xdb.sql.SqlTable;
import xht.xdb.sql.SqlTool;
import xht.xdb.sql.XDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Xdb是jdbc工具类，封装了常用的jdbc操作。
 * <pre>
 * Example：
 *  BasicDataSource dataSource = new BasicDataSource();
 *  dataSource.setUrl("jdbc:oracle:thin:@192.168.9.66:1521:orcl");
 *  dataSource.setUsername("xxx");
 *  dataSource.setPassword("xxx");
 *  dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
 *
 *  Xdb.init()
 *     .addDataSourceDefault(dataSource,DbType.ORACLE)
 *  Xdb.sql("insert into TEST_A (id,name,p_id) values( '2' , '1' ,'1')")
 *     .executeUpdate()
 *  Xdb.sqlFile(Test.class, "files/sql/device.sql")
 *     .sqlArgs(MapUtil.init().add("name",Arrays.asList("2",3l)))
 *     .executeQuery()
 *
 *  Connection conn = Xdb.getConnection();
 *  conn.setSavepoint("save1");
 *  Xdb.setConnection(conn);
 *  Savepoint savePoint = conn.setSavepoint();
 *  try{
 *     Xdb.sql("insert table1").executeUpdate();
 *     Xdb.sql("update table2").executeUpdate();
 *     conn.commit();
 *  }catch (Exception e){
 *     conn.rollback(savePoint);
 *  }
 *
 *
 * </pre>
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
@Slf4j
public class Xdb {
    private static final Xdb self = new Xdb();
    private final Map<String, XDataSource> dataSources = new Hashtable<>();
    private XDataSource xDataSourceDefault;
    private final ThreadLocal<XDataSource> xDataSourceSelected = new ThreadLocal<>();
    private final ThreadLocal<Connection> xConnection = new ThreadLocal<>();
    private static final String DEFAULT_DATASOURCE_NAME = "defaultDataSource";

    /**
     * Xdb 静态初始化方法
     *
     * @return 返回静态Xdb对象
     */
    public static Xdb init() {
        return Xdb.self;
    }

    /**
     * <pre>
     * 清除数据源
     * 参数个数为：
     * 1）0个时，清除全部数据源。
     * 2）1个或多个时，根据指定的数据源名称清除。
     * </pre>
     *
     * @param dataSourceNames 待删除的数据源名称
     * @return {@link Xdb}
     */
    public Xdb clearDatasSource(String... dataSourceNames) {
        if (dataSourceNames == null || dataSourceNames.length == 0) {
            this.dataSources.clear();
            return this;
        }
        for (String dataSourceName : dataSourceNames) {
            this.dataSources.remove(dataSourceName);
        }
        return this;
    }

    /**
     * <pre>
     * 添加数据源
     * 多数据源时需指定数据源（selectDataSourceByName）
     * </pre>
     *
     * @param dataSource     数据源
     * @param dbType         数据库类型
     * @param dataSourceName 数据源名称（多数据源时需指定）
     * @return 返回静态Xdb对象
     */
    public Xdb addDataSource(DataSource dataSource, DbType dbType, String dataSourceName) {
        XDataSource xDataSource = new XDataSource(dataSource, dbType, dataSourceName);
        this.dataSources.put(dataSourceName, xDataSource);
        return this;
    }

    /**
     * 添加并选择默认数据源（默认数据源名称为：Xdb.DEFAULT_DATASOURCE_NAME）
     *
     * @param dataSource 数据源
     * @param dbType     数据库类型
     * @return 返回静态Xdb对象
     */
    public Xdb addDataSourceDefault(DataSource dataSource, DbType dbType) {
        XDataSource xDataSource = new XDataSource(dataSource, dbType, DEFAULT_DATASOURCE_NAME);
        this.xDataSourceDefault = xDataSource;
        this.dataSources.put(DEFAULT_DATASOURCE_NAME, xDataSource);
        this.xDataSourceSelected.set(xDataSource);
        return this;
    }

    /**
     * 选择数据源(默认的)
     *
     * @return 返回静态Xdb对象
     */
    public static Xdb selectDataSourceDefault() {
        Xdb.self.xDataSourceSelected.set(Xdb.self.xDataSourceDefault);
        return Xdb.self;
    }

    /**
     * 选择指定数据源，作为默认数据源
     *
     * @param dataSourceName 数据源名称（多数据源时需指定）
     * @return 返回静态Xdb对象
     */
    public static Xdb selectDataSourceDefault(String dataSourceName) {
        Xdb.self.xDataSourceSelected.set(Xdb.self.xDataSourceDefault);
        return Xdb.self;
    }

    /**
     * 选择数据源
     *
     * @param dataSourceName 数据源名称（多数据源时需指定）
     * @return 返回静态Xdb对象
     */
    public static Xdb selectDataSourceByName(String dataSourceName) {
        XDataSource xDataSource = Xdb.self.dataSources.get(dataSourceName);
        Xdb.self.dataSources.put(DEFAULT_DATASOURCE_NAME, xDataSource);
        Xdb.self.xDataSourceSelected.set(xDataSource);
        return Xdb.self;
    }

    /**
     * 获取当前选择的数据源（封装了数据源名称、数据源类型）
     *
     * @return 当前选择的数据源
     */
    public static XDataSource getXDataSource() {
        XDataSource xDataSource = Xdb.self.xDataSourceSelected.get();
        if (xDataSource == null) {
            xDataSource = Xdb.self.xDataSourceDefault;
            Xdb.self.xDataSourceSelected.set(xDataSource);
        }
        return xDataSource;
    }

    /**
     * <pre>
     * 从当前选择的数据源中获取数据连接
     * 当前线程中已有connection时，返回此连接；
     * 当前线程中无connection或者connection失效时，创建新的连接
     * </pre>
     *
     * @return {@link Connection}
     */
    public static Connection getConnection() {
        try {
            Connection connection = Xdb.self.xConnection.get();
            if (connection == null) {
                connection = Xdb.getXDataSource().getDataSource().getConnection();
            } else {
                //连接失效时创建新的连接，与原链接的autoCommit保持一致
                if (connection.isClosed() || !connection.isValid(1)) {
                    boolean _autoCommit = connection.getAutoCommit();
                    connection = Xdb.getXDataSource().getDataSource().getConnection();
                    connection.setAutoCommit(_autoCommit);
                }
                Xdb.self.xConnection.set(connection);
            }
            return connection;
        } catch (SQLException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 从当前选择的数据源中设置数据连接
     *
     * @param connection 数据连接
     * @return Xdb
     */
    public static Xdb setConnection(Connection connection) {
        Xdb.self.xConnection.set(connection);
        return Xdb.self;
    }

    /**
     * <pre>
     * 功能：预处理sql
     * 1、sql文件中的参数使用冒号占位符，如" :id "," :name "
     * 2、冒号占位符前后必须留有空格
     * </pre>
     *
     * @param sql sql语句
     * @return SqlTool
     */
    public static SqlTool sql(String sql) {
        SqlTool sqlTool = getSqlTool();
        sqlTool.sql(sql);
        return sqlTool;
    }

    /**
     * <pre>
     * 功能：预处理sql
     * 1、sqlFileClass和sqlFileName需在同一目录层级
     * 2、sql文件中的参数使用冒号占位符，如" :id "," :name "
     * 3、冒号占位符前后必须留有空格
     * 4、注释语句以1行为单位，1行内的所有参数占位符如果都在sqlArgs中传入，则自动放开此行语句
     * </pre>
     *
     * @param sqlFileClass 与sql文件同目录的任意clazz（方便通过clazz获取类加载路径，从而找到sql文件）
     * @param sqlFileName  sql文件名
     * @return SqlTool
     */
    public static <T> SqlTool sqlFile(Class<T> sqlFileClass, String sqlFileName) {
        SqlTool sqlTool = getSqlTool();
        sqlTool.sqlFile(sqlFileClass, sqlFileName);
        return sqlTool;
    }

    private static SqlTool getSqlTool() {
        DbType dbType = DbType.SQLITE;
        XDataSource xDataSource = Xdb.getXDataSource();
        if (xDataSource != null) {
            dbType = xDataSource.getDbType();
        }
        return new SqlTool(dbType);
    }

    /**
     * <pre>
     * 功能：预处理sql
     * 1、sqlFileClass和sqlFileName需在同一目录层级
     * 2、sql文件中的参数使用冒号占位符，如" :id "," :name "
     * 3、冒号占位符前后必须留有空格
     * 4、注释语句以1行为单位，1行内的所有参数占位符如果都在sqlArgs中传入，则自动放开此行语句
     * </pre>
     *
     * @param sqlFileRelativePath sql文件相对路径,根目录默认位于xdb.sqlDir配置的目录下，未配置时默认为usr.dir对应目录
     * @return SqlTool
     */
    public static SqlTool sqlFile(String sqlFileRelativePath) {
        SqlTool sqlTool = getSqlTool();
        sqlTool.sqlFile(sqlFileRelativePath);
        return sqlTool;
    }

    /**
     * <pre>
     * 功能：sql插入
     *         Xdb.selectDataSourceByName("oracle");
     *         MapUtil args = MapUtil.init()
     *                 .add("id",1)
     *                 .add("name","1")
     *                 .add("ctime",new DATE());
     *         Xdb.table("test_a")
     *                 .sqlArgs(args)
     *                 .insert()
     *                 //.update()
     *                 ;
     * </pre>
     *
     * @param tableName 待插入的表名
     * @return {@link SqlTable}
     */
    public static SqlTable table(String tableName) {
        return new SqlTable(tableName);
    }

}
