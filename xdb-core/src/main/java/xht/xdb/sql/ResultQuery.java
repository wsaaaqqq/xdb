package xht.xdb.sql;

import xht.xdb.util.Underline2CamelUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * ResultQuery是sql查询结果的封装类
 */
@SuppressWarnings("unused")
@Slf4j
public class ResultQuery {
    @Override
    public String toString() {
        return "ResultQuery{" +
                "result=" + result +
                ", success=" + success +
                ", exception=" + exception +
                ", sql='" + sql + '\'' +
                '}';
    }

    /**
     * sql执行结果
     */
    private final List<Map<String, Object>> result;

    /**
     * sql是否成功执行标志符
     */
    private final boolean success;

    /**
     * sql执行出错时的异常信息
     */
    private final Exception exception;

    /**
     * 执行的sql
     */
    private final String sql;

    public ResultQuery(String sql, List<Map<String, Object>> result, boolean success, Exception exception) {
        this.sql = sql;
        this.result = result;
        this.success = success;
        this.exception = exception;
    }

    /**
     * <pre>
     *     返回查询结果集的第一个元素，若查询结果为空或执行失败时，返回空的map实例对象
     * </pre>
     *
     * @return Map
     */
    public Map<String, Object> first() {
        Map<String, Object> r = new HashMap<>();
        if (this.result.size() > 0) {
            r = this.result.get(0);
        }
        return r;
    }

    /**
     * <pre>
     *     返回查询结果集合，若查询结果为空或执行失败时返回空的list实例对象
     * </pre>
     *
     * @return List
     */
    public List<Map<String, Object>> result() {
        return this.result;
    }

    /**
     * 将查询结果集转换：列名（key值）转换为驼峰的格式
     *
     * @return List
     */
    public List<Map<String, Object>> toCamelList() {
        return formatList(ListFormatType.camel);
    }

    /**
     * 将查询结果集转换：列名（key值）转换为小写的格式
     *
     * @return List
     */
    public List<Map<String, Object>> toLowerCaseList() {
        return formatList(ListFormatType.lowerCase);
    }

    /**
     * 将查询结果集转换：列名（key值）转换为大写的格式
     *
     * @return List
     */
    public List<Map<String, Object>> toUpperCaseList() {
        return formatList(ListFormatType.upperCase);
    }

    /**
     * <pre>
     * 将group-count查询的结果集转换为map
     *
     * demo sql：
     * select
     *    type as g,
     *    count(1) as c
     * from xxx
     * group by type
     * </pre>
     *
     * @return Map
     */
    public Map<String, Object> toGroupCount() {
        return toGroupCount("g", "c");
    }

    /**
     * <pre>
     * 将group-count查询的结果集转换为map
     * demo java:
     * .toGroupCount("g1","c1")
     *
     * demo sql：
     * select
     *    type as g1,
     *    count(1) as c1
     * from xxx
     * group by type
     * </pre>
     *
     * @return Map
     */
    public Map<String, Object> toGroupCount(String groupKey, String countKey) {
        Map<String, Object> r = new HashMap<>();
        if (this.result != null) {
            for (Map<String, Object> map : result) {
                r.put(String.valueOf(map.get(groupKey)), map.get(countKey));
            }
        }
        return r;
    }

    /**
     * <pre>
     *     打印结果，同时打印集合元素个数
     * </pre>
     *
     * @return ResultQuery
     */
    public ResultQuery out() {
        List<Map<String, Object>> list = this.result();
        for (Object o : list) {
            log.debug("{}",o);
        }
        log.debug("count: {}",list.size());
        return this;
    }

    /**
     * <pre>
     *     1、执行sql时，如果出现错误则抛出RuntimeException包装的运行时异常
     *     2、由于sql执行错误时以设置返回默认值，所以本方法主要用于异常处理、事务处理
     * </pre>
     *
     * @return ResultQuery
     */
    public ResultQuery throwExcetpionWhenErr() {
        if (!success) {
            throw new RuntimeException(exception);
        }
        return this;
    }

    private List<Map<String, Object>> formatList(ListFormatType type) {
        List<Map<String, Object>> r = new ArrayList<>();
        for (Map<String, Object> m : this.result) {
            Map<String, Object> _m = new HashMap<>();
            Set<String> keys = m.keySet();
            for (String key : keys) {
                String _key = null;
                if (ListFormatType.upperCase.equals(type)) {
                    _key = key.toUpperCase();
                } else if (ListFormatType.camel.equals(type)) {
                    _key = Underline2CamelUtil.underline2Camel(key, true);
                } else if (ListFormatType.lowerCase.equals(type)) {
                    _key = key.toLowerCase();
                }
                Object v = m.get(key);
                _m.put(_key, v);
            }
            r.add(_m);
        }
        return r;
    }

}
