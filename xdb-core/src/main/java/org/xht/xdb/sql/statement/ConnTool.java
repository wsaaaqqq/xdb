package org.xht.xdb.sql.statement;

import org.xht.xdb.util.Closes;
import org.xht.xdb.util.MapUtil;
import org.xht.xdb.Xdb;
import org.xht.xdb.sql.ResultBatch;
import org.xht.xdb.sql.ResultQuery;
import org.xht.xdb.sql.ResultUpdate;
import org.xht.xdb.util.ResultSetUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ConnTool {

    public static ResultUpdate execute(String sql, MapUtil sqlArgs, boolean... autoCloseConnection) {
        ResultUpdate r;
        Connection conn = null;
        try {
            conn = Xdb.getConnection();
            int ret = sqlArgs(conn, sql, sqlArgs).executeUpdate();
            r = new ResultUpdate(sql, ret, true, null);
        } catch (Exception e) {
            r = new ResultUpdate(sql, -1, false, e);
            log.error("", e);
        } finally {
            if (autoCloseConnection == null || autoCloseConnection.length == 0 || autoCloseConnection[0]) {
                Closes.closeConnection(conn);
            }
        }
        return r;
    }

    /**
     * <pre>
     *     执行查询语句，返回查询结果集合，若查询结果为空或执行失败时返回空的list实例对象
     * </pre>
     *
     * @return ResultQuery
     */
    public static ResultQuery executeQuery(String sql, MapUtil sqlArgs, boolean... autoCloseConnection) {
        ResultQuery r;
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = null;
        try {
            conn = Xdb.getConnection();
            ResultSet result = sqlArgs(conn, sql, sqlArgs).executeQuery();
            list = ResultSetUtil.toList(result);
            r = new ResultQuery(sql, list, true, null);
        } catch (Exception e) {
            r = new ResultQuery(sql, list, false, e);
            log.error("", e);
        } finally {
            if (autoCloseConnection == null || autoCloseConnection.length == 0 || autoCloseConnection[0]) {
                Closes.closeConnection(conn);
            }
        }
        return r;
    }

    private static NamedParameterStatement sqlArgs(Connection conn, String sql, MapUtil sqlArgs) throws SQLException {
        NamedParameterStatement statement = new NamedParameterStatement(conn, sql);
        Set<String> keys = sqlArgs.keySet();
        for (String key : keys) {
            Object value = sqlArgs.get(key);
            if (value == null) {
                String _sql = sql.toLowerCase();
                //先简单处理，后续需排除insert xxx select xxx from where id=:id, id为空的情况
                //insert value值可以为空，update的新值也可以为空（oracle已测试）
                if (sql.contains("insert") || _sql.contains("update")) {
                    statement.setNull(key, Types.NULL);
                } else {
                    //此处处理： sql = "... where id=:id"，当参数为null时执行报错的问题，但
                    throw new SQLException(String.format(
                            "参数%s不能为空：sql语句应直接写 where (id is null or id=:id)", key
                    ));
                }
            } else if (Long.class.isAssignableFrom(value.getClass())) {
                long vLong = (Long) value;
                statement.setLong(key, vLong);
            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                statement.setBigDecimal(key, (BigDecimal) value);
            } else if (BigInteger.class.isAssignableFrom(value.getClass())) {
                BigInteger vBigInteger = (BigInteger) value;
                BigDecimal vBigDecimal = new BigDecimal(vBigInteger);
                statement.setBigDecimal(key, vBigDecimal);
            } else if (Character.class.isAssignableFrom(value.getClass())) {
                String vStringInteger = String.valueOf(value);
                statement.setString(key, vStringInteger);
            } else if (java.sql.Timestamp.class.isAssignableFrom(value.getClass())) {
                java.sql.Timestamp vDate = (Timestamp) value;
                statement.setTimestamp(key, vDate);
            } else if (java.sql.Date.class.isAssignableFrom(value.getClass())) {
                java.sql.Date vDate = ((java.sql.Date) value);
                statement.setDate(key, vDate);
            } else if (java.sql.Time.class.isAssignableFrom(value.getClass())) {
                java.sql.Time vDate = (java.sql.Time) value;
                statement.setTime(key, vDate);
            } else if (java.util.Date.class.isAssignableFrom(value.getClass())) {
                java.sql.Date vDate = new java.sql.Date(((java.util.Date) value).getTime());
                statement.setDate(key, vDate);
            } else {
                statement.setObject(key, value);
            }
        }
        return statement;
    }

    public static ResultBatch executeBatch(String sql, List<Object[]> values, int batchSize, boolean autoCommit, boolean... autoCloseConnection) {
        ResultBatch resultBatch;
        List<int[]> result = new ArrayList<>();
        Connection conn = Xdb.getConnection();
        try {
            assert conn != null;
            conn.setAutoCommit(false);
            PreparedStatement statement = conn.prepareStatement(sql);
            for (int i = 0, j = 0, len = values.size(); i < len; i++) {
                Object[] value = values.get(i);
                for (int k = 0, l = value.length; k < l; k++) {
                    statement.setObject(k + 1, value[k]);
                }
                statement.addBatch();
                if (j == batchSize) {
                    j = 1;
                    int[] r = statement.executeBatch();
                    result.add(r);
                }
                j++;
            }
            int[] r = statement.executeBatch();
            result.add(r);
            if (autoCommit) {
                conn.commit();
                conn.setAutoCommit(true);
            }
            resultBatch = new ResultBatch(sql, result, true, null);
        } catch (SQLException e) {
            log.error("", e);
            resultBatch = new ResultBatch(sql, result, false, e);
        } finally {
            if (autoCloseConnection == null || autoCloseConnection.length == 0 || autoCloseConnection[0]) {
                Closes.closeConnection(conn);
            }
        }
        return resultBatch;
    }

}
