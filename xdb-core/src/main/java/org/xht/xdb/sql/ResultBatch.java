package org.xht.xdb.sql;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * ResultCommon是sql执行结果常规封装类
 */
@SuppressWarnings("unused")
@Slf4j
public class ResultBatch {
    /**
     * sql执行结果
     */
    private final List<int[]> result;

    /**
     * sql是否成功执行标志符
     */
    private final boolean success;

    @Override
    public String toString() {
        return "ResultUpdate{" +
                "result=" + result +
                ", success=" + success +
                ", exception=" + exception +
                ", sql='" + sql + '\'' +
                '}';
    }

    /**
     * sql执行出错时的异常信息
     */
    private final Exception exception;
    /**
     * 执行的sql
     */
    private final String sql;

    public ResultBatch(String sql, List<int[]> result, boolean success, Exception exception) {
        this.sql = sql;
        this.result = result;
        this.success = success;
        this.exception = exception;
    }

    /**
     * 返回sql执行结果
     *
     * @return sql执行结果
     */
    public List<int[]> result() {
        return result;
    }

    /**
     * <pre>
     *     打印结果
     * </pre>
     *
     * @return ResultUpdate
     */
    public ResultBatch out() {
        log.debug("success: {}",this.success);
        log.debug("result:");
        for (int[] ints : this.result) {
            log.debug(Arrays.toString(ints));
        }
        return this;
    }

    /**
     * <pre>
     *     1、执行sql时，如果出现错误则抛出RuntimeException包装的运行时异常
     *     2、由于sql执行错误时以设置返回默认值，所以本方法主要用于异常处理、事务处理
     * </pre>
     *
     * @return ResultUpdate
     */
    public ResultBatch throwExceptionWhenErr() {
        if (!success) {
            throw new RuntimeException(exception);
        }
        return this;
    }

}
