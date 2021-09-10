package xht.xdb.util;


import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Slf4j
public class ResultSetUtil {
    /**
     * 显示结果集
     *
     * @param rs rs
     * @throws SQLException 异常
     */
    public static void out(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount() + 1;
        while (rs.next()) {
            log.debug("{}",toMap(rs, md, colCount, true));
        }
    }

    /**
     * rs to list key一般为“XX_ID”
     *
     * @param rs rs
     * @return list
     * @throws SQLException SQLException
     */
    public static List<Map<String, Object>> toList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount() + 1;
        while (rs.next()) {
            rows.add(toMap(rs, md, colCount, false));
        }
        return rows;
    }

    /**
     * rs to list 驼峰形式
     *
     * @param rs rs
     * @return List
     * @throws SQLException SQLException
     */
    public static List<Map<String, Object>> toListCapm(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount() + 1;
        while (rs.next()) {
            rows.add(toMap(rs, md, colCount, true));
        }
        return rows;
    }

    private static Map<String, Object> toMap(ResultSet rs, ResultSetMetaData md, int colCount, boolean isCamel)
            throws SQLException {
        Map<String, Object> row = new HashMap<>();
        String key;
        for (int i = 1; i < colCount; i++) {
            if (isCamel) {
                key = Underline2CamelUtil.underline2Camel(md.getColumnName(i), true);
            } else {
                key = md.getColumnName(i);
            }
            row.put(key, rs.getObject(i));
        }
        return row;
    }

}
