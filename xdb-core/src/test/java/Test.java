import xht.xdb.Xdb;
import xht.xdb.util.MapUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Test {
    public static void main(String[] args) {
        Init.oracle();
//        Init.sqlite();
//        Init.dameng();
        test();
    }

    private static void test() {
        Xdb.selectDataSourceByName("oracle");
//        long l = Xdb.sql("update T_SJ_LOG set login_name=:login_name where login_name='111'")
//                .sqlArgs(MapUtil.init().add("login_name",null)
//                )
//                .executeUpdate().result();
//        log.debug("{}",l);
        long l = Xdb.sql("select * from T_SJ_LOG where login_name is :login_name")
                .sqlArgs(MapUtil.init().add("login_name",null)
                )
                .executeCount();
        log.debug("{}",l);
//        MapUtil values = MapUtil.init()
//                .add("id",1)
//                .add("name","2")
//                ;
//        Log.println(Xdb.table("test_a")
//                .values(values)
//                .insert()
//        );
    }

}
