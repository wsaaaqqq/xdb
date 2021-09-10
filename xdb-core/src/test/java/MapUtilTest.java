import xht.xdb.util.MapUtil;

import java.util.Map;

public class MapUtilTest {
    public static void main(String[] args) {
        MapUtil init = MapUtil.init();
        init.add("czId", "id1");
        init.add("age", 1);
        Map<String,Object> map = init
                .value();
        MapUtil m1 = MapUtil.wrap(map);
        MapUtil m2 = MapUtil.wrap(map);
        m2.add("xx",1);
        m1.debug();
        m2.debug();
    }
}
