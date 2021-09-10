import xht.xdb.util.ListUtil;
import xht.xdb.util.MapUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ListUtilTest {

    public static void main(String[] args) {
        List<Map<String,Object>> list = new ArrayList<>();
        list.add(MapUtil.init().add("id","1").add("name","1").value());
        list.add(MapUtil.init().add("id","2").add("name","1").value());
        list.add(MapUtil.init().add("id","3").add("name","1").value());
        list.add(MapUtil.init().add("id","4").add("name","1").value());

        List<Object[]> objects = ListUtil.listMap2Object(list, "id,name");
        for (Object[] object : objects) {
            for (Object o : object) {
                log.debug("{}",o);
                log.debug("   ");
            }
            log.debug("-------------------------");
        }
    }
}
