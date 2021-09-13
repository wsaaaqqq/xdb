import org.xht.xdb.Xdb;
import org.xht.xdb.util.ListUtil;
import org.xht.xdb.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BatchTest {
    public static void main(String[] args) {
        Init.sqlite();
        Xdb.sql("delete from test").executeUpdate();
//        test1();
//        test2();
    }


    public static void test1() {
        String sql = "insert into test (name,id,age) values (:name ,'A',:age )";
        List<Map<String,Object>> values2 = new ArrayList<>();
        for(int i=0;i<11;i++){
            values2.add(MapUtil.init().add("name","\"\"").add("age",i).value());
        }
        List<Object[]> values = ListUtil.listMap2Object(values2,"name,age");
        Xdb.sql(sql).executeBatch(values,1000,true);
        Xdb.sql("select * from test where age in(:age,4)")
                .sqlArgs(MapUtil.init().add("age", Arrays.asList(1,2,3)))
                .executeQuery().out();
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
