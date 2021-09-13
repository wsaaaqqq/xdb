import org.xht.xdb.Xdb;
import org.xht.xdb.enums.DbType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp.BasicDataSource;

@Slf4j
public class ThreadTest {
    public static void main(String[] args) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@192.168.9.66:1521:orcl");
        dataSource.setUsername("orcl");
        dataSource.setPassword("orcl");
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        Xdb.init().addDataSource(dataSource, DbType.ORACLE, "oracle");

        for (int i = 0; i < 1000; i++) {
            log.debug(String.format("thread %s start",i));
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Xdb.selectDataSourceByName("oracle");
                    Xdb.sql("select * from t_web_sbgl_zd").executeQuery();
                    log.debug(String.format("thread %s end",Thread.currentThread().getName()));
                }
            });
            t.setName(""+i);
            t.start();
        }
    }
}
