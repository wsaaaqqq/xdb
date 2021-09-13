package org.xht.xdb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListUtil {

    public static List<Object[]> listMap2Object(List<Map<String, Object>> maps, String keyOrders) {
        List<Object[]> ret = new ArrayList<>();
        if (keyOrders != null) {
            String[] keys = keyOrders.split(",");
            int len = keys.length;
            for (Map<String, Object> map : maps) {
                Object[] obj = new Object[len];
                if (map != null) {
                    for (int i = 0; i < len; i++) {
                        obj[i] = map.get(keys[i]);
                    }
                }else {
                    for (int i = 0; i < len; i++) {
                        obj[i] = null;
                    }
                }
                ret.add(obj);
            }
        }
        return ret;
    }
}
