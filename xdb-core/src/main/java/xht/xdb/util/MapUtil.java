package xht.xdb.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * MapUtil是Map的工具类，以Stream的方式操作Map实例。
 */
@SuppressWarnings({"unused", "unchecked"})
@Slf4j
public class MapUtil {

    private Map<String, Object> self;

    /**
     * 静态初始化方法
     *
     * @return this
     */
    public static MapUtil init() {
        MapUtil maps = new MapUtil();
        maps.self = new HashMap<>();
        return maps;
    }

    /**
     * 向Map中添加键值对
     *
     * @param key   键
     * @param value 值
     * @return MapUtil
     */
    @SuppressWarnings("UnusedReturnValue")
    public MapUtil add(String key, Object value) {
        self.put(key, value);
        return this;
    }

    public MapUtil addOnlyNotNull(String key, Object value) {
        if (value != null) {
            self.put(key, value);
        }
        return this;
    }

    /**
     * 删除键值对
     *
     * @param key 键
     * @return MapUtil
     */
    public MapUtil del(String key) {
        self.remove(key);
        return this;
    }

    /**
     * 克隆
     *
     * @return MapUtil
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public MapUtil clone() {
        MapUtil r = new MapUtil();
        Set<String> keys = self.keySet();
        for (String key : keys) {
            r.add(key, self.get(key));
        }
        return r;
    }

    /**
     * 克隆（只克隆指定keys）
     *
     * @param keys 待克隆的keys
     * @return MapUtil
     */
    public MapUtil clone(String... keys) {
        MapUtil r = new MapUtil();
        if (keys != null) {
            for (String key : keys) {
                Object value = self.get(key);
                if (value != null) {
                    r.add(key, value);
                }
            }
        }
        return r;
    }

    /**
     * 根据key获取value
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return self.get(key);
    }

    /**
     * 返回key集合
     *
     * @return keys
     */
    public Set<String> keySet() {
        return self.keySet();
    }

    @Override
    public String toString() {
        return self.toString();
    }

    public String toJson() {
        return new Gson().toJson(self);
    }

    /**
     * 返回Map实例对象
     *
     * @return 返回Map实例对象
     */
    public Map<String, Object> value() {
        return self;
    }

    /**
     * 封装 map对象为 MapUtil对象（保留原map的引用）
     *
     * @param map map
     * @return MapUtil
     */
    public static MapUtil wrap(Map<String, Object> map) {
        MapUtil r = MapUtil.init();
        r.self = map;
        return r;
    }

    /**
     * 封装 map对象为 MapUtil对象（保留原map的引用）
     *
     * @param json json
     * @return MapUtil
     */
    public static MapUtil wrap(String json) throws JsonSyntaxException {
        MapUtil r = MapUtil.init();
        r.self = new Gson().fromJson(json, HashMap.class);
        return r;
    }

    /**
     * 封装 map对象为 MapUtil对象（保留原map的引用）
     *
     * @param jsonObject jsonObject
     * @return MapUtil
     */
    public static MapUtil wrap(JsonObject jsonObject) throws JsonSyntaxException {
        MapUtil r = MapUtil.init();
        r.self = new Gson().fromJson(jsonObject, HashMap.class);
        return r;
    }

    /**
     * 封装 map对象为 MapUtil对象(复制新的一份map)
     *
     * @param map map
     * @return MapUtil
     */
    public static MapUtil wrapClone(Map<String, Object> map) {
        MapUtil r = MapUtil.init();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            r.add(key, map.get(key));
        }
        return r;
    }

    /**
     * <pre>
     * 转换map中的key
     *
     * input：
     *      {userId:1,userName:1} ,  "userId=user_id,userName=user_name"
     * output:
     *      {user_id:1,user_name:1}
     * </pre>
     *
     * @param map1       {userId:1,userName:1}
     * @param keyFormats "userId=user_id,userName=user_name"
     * @return {@link Map}
     */
    @SuppressWarnings("rawtypes")
    public static Map formatKey(Map<String, Object> map1, String keyFormats) {
        Map<String, String> aliasesMap = new HashMap<>();
        String[] split = keyFormats.split(",");
        for (String s : split) {
            String[] split1 = s.split("=");
            aliasesMap.put(split1[0], split1[1]);
        }
        Set<String> aliasesKeySet = aliasesMap.keySet();
        Set<String> keySet = map1.keySet();
        Map<String, Object> map2 = new HashMap<>();
        for (String key : keySet) {
            if (aliasesKeySet.contains(key)) {
                map2.put(aliasesMap.get(key), map1.get(key));
            } else {
                map2.put(key, map1.get(key));
            }
        }
        return map2;
    }

    /**
     * debug
     *
     * @return MapUtil
     */
    @SuppressWarnings("UnusedReturnValue")
    public MapUtil debug() {
        log.debug("{}", self);
        return this;
    }

    /**
     * <pre>
     * 转换map中的key
     *
     * input：
     *      {userId:1,userName:1} ,  "userId=user_id,userName=user_name"
     * output:
     *      {user_id:1,user_name:1}
     * </pre>
     *
     * @param keyFormats 关键的别名
     * @return {@link MapUtil}
     */
    public MapUtil formatKey(String keyFormats) {
        self = formatKey(self, keyFormats);
        return this;
    }

}
