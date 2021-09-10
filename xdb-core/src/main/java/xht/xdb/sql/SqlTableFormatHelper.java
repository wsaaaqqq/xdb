package xht.xdb.sql;

import xht.xdb.util.MapUtil;

import java.util.Map;
import java.util.Set;

/**
 * sql执行工具
 */
@SuppressWarnings("unused")
public class SqlTableFormatHelper {

    public static String insert(String tableName, MapUtil sqlArgs) {
        StringBuilder keys = new StringBuilder();
        StringBuilder values = new StringBuilder();
        Map<String, Object> args = sqlArgs.value();
        Set<String> keySet = args.keySet();
        for (String key : keySet) {
            keys.append(key).append(" , ");
            values.append(":").append(key).append(" , ");
        }
        if (keySet.size() > 0) {
            keys = new StringBuilder(keys.substring(0, keys.length() - 3));
            values = new StringBuilder(values.substring(0, values.length() - 3));
        }
        return String.format("insert into %s ( %s ) values ( %s )", tableName, keys.toString(), values.toString());
    }

}
