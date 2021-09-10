import xht.xdb.Xdb;
import xht.xdb.util.ListUtil;
import xht.xdb.util.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BatchTest2 {
    public static void main(String[] args) {
        Init.oracle();
        Xdb.sql("delete from T_TF_CZCS").executeUpdate();
        test1();
//        test2();
    }

    public static void test1() {
        String sql = "INSERT ALL INTO T_TF_CZCS(id,SHR) VALUES (1,1) INTO T_TF_CZCS(id,SHR) VALUES (2,1) SELECT 1 FROM DUAL";
        Xdb.sql(sql)
                .executeUpdate();
        Xdb.sql("select * from t_tf_czcs")
                .executeQuery()
                .out();
    }

    public static void test2() {
        String sql = "insert into test (id,name) values (:name,:id)";
//        String sql = "insert into test (id,name) values (:id,:name)";
//        String sql = "insert into test (id,name) values (?,?)";
//        String sql = "insert into test (name,id) values (?,?)";
        List<Object[]> values;
        List<Map<String,Object>> values2 = new ArrayList<>();
        for(int i=0;i<4;i++){
//            values.add(new Object[]{i,"name-"+i});
            values2.add(MapUtil.init().add("id",i).add("name","name-"+i).value());
        }
        values = ListUtil.listMap2Object(values2,"id,name");
        Xdb.sql(sql).executeBatch(values,1000,true);
        Xdb.sql("select * from test").executeQuery().out();
    }
}
