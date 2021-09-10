import xht.xdb.Xdb;
import xht.xdb.enums.DbType;
import org.apache.commons.dbcp.BasicDataSource;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

public class Init {

    public static void oracle() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:orcl");
        dataSource.setUsername("rlst");
        dataSource.setPassword("rlst");
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        Xdb.init().addDataSource(dataSource, DbType.ORACLE, "oracle");
        Xdb.selectDataSourceByName("oracle");
    }

    public static void sqlite() {
        SQLiteDataSource dataSource = new SQLiteConnectionPoolDataSource();
    dataSource.setUrl("jdbc:sqlite:file:/D:/workspace/xdb/src/test/java/test.db");
        Xdb.init().addDataSource(dataSource, DbType.SQLITE, "sqlite");
        Xdb.selectDataSourceByName("sqlite");
    }

    public static void dameng() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:dm://192.168.10.17:5236/xxx");
        dataSource.setUsername("xxx");
        dataSource.setPassword("xxx");
        dataSource.setDriverClassName("dm.jdbc.driver.DmDriver");
        Xdb.init()
                .addDataSource(dataSource, DbType.DAMENG, "dameng")
        ;
        Xdb.selectDataSourceByName("dameng");
    }

    public static void main(String[] args) {
        dameng();
        CodeRunTimeUtil.start();
        Xdb.sql("select * from ( select count(1) as col_0_0_ from V_WEB_SBGL_ECSBJB vwebsbglec0_ where vwebsbglec0_.GLDW_ID='ff808081795b787e01795c1ba64d00bf' ) where rownum<=20")
                .executeQuery()
                .out();
        CodeRunTimeUtil.cost();
        Xdb.sql("select * from ( select count(1) as col_0_0_ from V_WEB_SBGL_ECSBJB vwebsbglec0_ where vwebsbglec0_.GLDW_ID='ff808081795b787e01795c1ba64d00bf' ) limit 20")
                .executeQuery()
                .out();
        CodeRunTimeUtil.cost();
    }
}
