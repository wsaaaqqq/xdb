import org.xht.xdb.Xdb;
import org.xht.xdb.util.MapUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommitTest {
    public static void main(String[] args) throws SQLException {
        Init.sqlite();
        Xdb.sql("delete from test").executeUpdate();
//        Xdb.sql("select count(1) from test").executeQuery().out();
        Connection conn = Xdb.getConnection();
        conn.setAutoCommit(false);
        String sql = "insert into test (id) values (:id)";
        Xdb.sql(sql).sqlArgs(MapUtil.init().add("id",1)).executeUpdate(false);
        conn.commit();
        PreparedStatement statement = conn.prepareStatement(sql);
        for (int i=0,j = 0; i < 1000000; i++) {
            statement.addBatch();
            if(j==1000){
                j=1;
                statement.executeBatch();
            }
            j++;
        }
        statement.executeBatch();
        conn.commit();
        Xdb.sql("select * from test").executeQuery().out();
//        Log.println("----------------------------------------------");
//        Xdb.getConnection().setAutoCommit(true);
//        for (int i = 0; i < 100; i++) {
//            Log.println(Xdb.getConnection().getAutoCommit());
//        }
    }
}
