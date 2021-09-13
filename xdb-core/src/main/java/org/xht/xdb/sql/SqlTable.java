package org.xht.xdb.sql;

import org.xht.xdb.sql.statement.ConnTool;
import org.xht.xdb.util.BeanUtil;
import org.xht.xdb.util.MapUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * sql执行工具
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@Slf4j
public class SqlTable {
    protected String sql;
    protected String tableName;
    protected MapUtil values;

    /**
     * 构造 SqlTable
     *
     * @param tableName 待插入的表名
     */
    public SqlTable(String tableName) {
        this.values = MapUtil.init();
        this.tableName = tableName;
    }

    /**
     * <pre>
     * 设置sql语句values部分的参数
     *      values可为一下3种类型：
     *          1）map类型 :  {id:1,name:2}
     *          2）Bean类型 :  {id:1,name:2}
     *          3）MapUtil类型 :  {id:1,name:2}
     * </pre>
     *
     * @param values sql语句values部分的参数
     * @return SqlTool
     */
    public <T> SqlTable values(T values) {
        if (values != null) {
            if (values instanceof MapUtil) {
                this.values = (MapUtil) values;
            } else if (values instanceof Map) {
                //noinspection unchecked
                this.values = MapUtil.wrapClone((Map<String, Object>) values);
            } else {
                Map<String, Object> map = BeanUtil.toMap(values);
                this.values = MapUtil.wrapClone(map);
            }
        }
        return this;
    }

    /**
     * <pre>
     * 设置sql语句values部分的参数
     * inputs:
     *      values:
     *          1）map类型 :  {id:1,name:2}
     *          2）Bean类型 :  {id:1,name:2}
     *          3）MapUtil类型 :  {id:1,name:2}
     *      keyFormats:
     *          "userId=user_id,userName=user_name"
     * </pre>
     *
     * @param values sql语句values部分的参数
     * @return SqlTool
     */
    public <T> SqlTable values(T values, String keyFormats) {
        values(values);
        this.values = this.values.formatKey(keyFormats);
        return this;
    }

    /**
     * <pre>
     * 执行insert sql：
     * 1）dml类sql返回变更的记录个数
     * 2）返回0或者没有返回结果的sql执行
     * </pre>
     *
     * @return {@link ResultUpdate}
     */
    public ResultUpdate insert() {
        this.sql = SqlTableFormatHelper.insert(this.tableName, this.values);
        return ConnTool.execute(this.sql, this.values);
    }

    /**
     * <pre>
     * 打印封装后的sql（参数、分页、limit）
     * </pre>
     *
     * @return {@link SqlTable}
     */
    public SqlTable debug() {
        this.sql = SqlTableFormatHelper.insert(this.tableName, this.values);
        log.debug(this.sql);
        log.debug("{}",this.values);
        return this;
    }

}
