import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;

/**
 * 代码运行时工具类
 *
 * @author Administrator
 * @date 2021/05/19
 */
@SuppressWarnings("unused")
@Slf4j
public class CodeRunTimeUtil {
    private static ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 开始
     */
    public static long start() {
        long time = System.currentTimeMillis();
        startTime.set(time);
        return time;
    }


    /**
     * 设置开始
     *
     * @param time 时间
     * @return long
     */
    public static long setStart(long time) {
        startTime.set(time);
        return time;
    }


    /**
     * 开始
     *
     * @param msg 提示信息
     */
    public static long start(String msg) {
        log.debug("{}", msg);
        return start();
    }

    /**
     * 耗时
     */
    public static long cost() {
        return cost("");
    }

    /**
     * 耗时
     *
     * @param msg 提示信息
     */
    public static long cost(String msg) {
        long currentTime = System.currentTimeMillis();
        Long _startTime = startTime.get();
        if (_startTime != null) {
            log.debug("{} cost: {} (ms) ", msg,
                    new DecimalFormat("#,###").format(currentTime - _startTime));
        }
        startTime.set(currentTime);
        return currentTime;
    }

}
