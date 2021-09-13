import org.xht.xdb.Xdb;
import org.xht.xdb.util.MapUtil;

import java.util.Arrays;

public class Table {
    public static void main(String[] args) {
        Init.sqlite();
        Xdb.sql("select * from test where age in( :age  )")
                .sqlArgs(
                        MapUtil.init()
                                .add("age", Arrays.asList(1,2,3))
                )
//                .debug()
                .executeQuery().out()
                ;
//        Xdb.table("aa")
//                .values(MapUtil.init()
//                    .add("id","1")
//                    .add("name","2")
//                )
//                .insert();
    }
}
