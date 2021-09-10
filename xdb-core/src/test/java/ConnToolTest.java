import xht.xdb.util.MapUtil;

import java.util.Map;

public class ConnToolTest {
    public static void main(String[] args) {
        String sql = ":name,:id";
        Map<String,Object> t = MapUtil.init().add("id",1).add("name","name1").value();
//        Object[] arr = ConnTool.valuesOrdered(sql,t);
//        for (int i = 0; i < arr.length; i++) {
//            Log.println(arr[i]);
//        }
    }
}
