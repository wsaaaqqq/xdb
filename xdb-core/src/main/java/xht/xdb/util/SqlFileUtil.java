package xht.xdb.util;

import xht.xdb.Xdb;
import xht.xdb.XdbConfig;
import xht.xdb.enums.DbType;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
public class SqlFileUtil {

    /**
     * <pre>
     * 功能：动态组装sql
     *    1、sqlFileClass和sqlFileName需在同一目录层级
     *    2、sql文件中的参数使用冒号占位符，如" :id "," :name "
     *    3、冒号占位符前后必须留有空格
     *    4、注释语句以1行为单位，1行内的所有参数占位符如果都在sqlArgs中传入，则自动放开此行语句
     * </pre>
     *
     * @param sqlFileClass 与sql文件同目录的任意clazz（方便通过clazz获取类加载路径，从而找到sql文件）
     * @param sqlFileName  sql文件名
     * @param sqlArgs      sql查询参数
     * @return sql
     */
    public static <T> String getSql(Class<T> sqlFileClass, String sqlFileName, MapUtil sqlArgs) {
        InputStream is = getInputStream(sqlFileClass, sqlFileName);
        return getContext(is, sqlArgs);
    }

    private static InputStream getInputStream(Class<?> sqlFileClass, String sqlFileName) {
        DbType dbType = Xdb.getXDataSource().getDbType();
        String sqlFileNameCopy = sqlFileName;
        //优先查找.sql.oracle和.sql.sqlite后缀的sql文件，找不到时才使用.sql后缀的sql文件
        sqlFileName = DbType.getSqlFileSuffix(sqlFileName, dbType);
        URL url = sqlFileClass.getResource(sqlFileName);
        if (url == null) {
            url = sqlFileClass.getResource(sqlFileNameCopy);
        }
        InputStream is = null;
        try {
            is = url.openStream();
        } catch (IOException e) {
            log.error("", e);
        }
        return is;
    }

    private static String getContext(InputStream is, MapUtil sqlParams) {
        String sql = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        String line;
        StringBuffer sqlBuffer = new StringBuffer();
        boolean multyComment = false;
        try {
            isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            reader = new BufferedReader(isr);
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--")) {
                    if (sqlParams != null) {
                        line = line.replace("--", "");
                        String realLine = line;
                        Set<String> keys = sqlParams.keySet();
                        if (keys != null) {
                            for (String key : keys) {
                                line = line.replaceAll(":".concat(key), "");
                            }
                            if (!line.contains(":")) {
                                sqlBuffer.append(" ").append(realLine).append(" ");
                            }
                        }
                    }
                    continue;
                }
                if (line.startsWith("/*")) {
                    multyComment = true;
                    continue;
                }
                if (multyComment) {
                    if (line.endsWith("*/")) {
                        multyComment = false;
                    }
                    continue;
                }
                sqlBuffer.append(line).append(" ");
            }
            sql = new String(sqlBuffer);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            close(is);
            close(isr);
            close(reader);
        }
        return sql;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    /**
     * 获得sql
     * <pre>
     * 功能：动态组装sql
     *    1、 XdbConfig.setSqlDir
     *    2、sql文件中的参数使用冒号占位符，如" :id "," :name "
     *    3、冒号占位符前后必须留有空格
     *    4、注释语句以1行为单位，1行内的所有参数占位符如果都在sqlArgs中传入，则自动放开此行语句
     * </pre>
     *
     * @param sqlFileRelativePath sql文件相对路径
     * @param sqlArgs             sql查询参数
     * @return sql
     */
    public static String getSql(String sqlFileRelativePath, MapUtil sqlArgs) {
        String sqlDir = XdbConfig.getSqlDir();
        if (sqlDir == null) {
            sqlDir = System.getProperty("user.dir");
            XdbConfig.setSqlDir(sqlDir);
        }
        File file = new File(sqlDir + File.separator + sqlFileRelativePath);
        try {
            InputStream is = new FileInputStream(file);
            return getContext(is, sqlArgs);
        } catch (Exception e) {
            log.error("sql file not found: {}",file.getAbsolutePath(), e);
        }
        return null;
    }

}
