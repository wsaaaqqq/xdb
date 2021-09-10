package xht.xdb.util;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashMap;

class MapUtilTest {
    public static void main(String[] args) {
        String json = MapUtil.init().add("name", "a")
                .add("arr", new String[]{"s1", "s2", "s3"})
                .add("list", Arrays.asList(1, 2, 3))
                .toJson();
        HashMap<String, Object> hashMap = new Gson().fromJson(json, HashMap.class);
        System.out.println(MapUtil.wrap(hashMap));
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        System.out.println(MapUtil.wrap(jsonObject));
    }

}