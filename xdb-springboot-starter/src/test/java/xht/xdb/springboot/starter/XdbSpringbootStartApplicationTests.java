package xht.xdb.springboot.starter;

import xht.xdb.Xdb;
import xht.xdb.sql.SqlTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
//@SpringBootApplication
//@EnableTransactionManagement
//@EnableJdbcRepositories
@EnableAutoConfiguration
@SpringBootConfiguration
class XdbSpringbootStartApplicationTests {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    void test1() {
        List<Map<String, Object>> maps = Xdb.sqlFile("user_query1.sql")
                .sqlArg("ids", Arrays.asList(1, 2, 3))
                .sqlArg("info","info")
                .pageIndex(1)
                .pagePerSize(2)
                .executeQuery()
                .result();
        maps.forEach(System.out::println);
    }

    @Test
    void test2() {
        SqlTool st = Xdb
//                .sqlFile("user_query1.sql")
//                .sqlFile("user_query1")
                .sqlFile("user/user_query1")
                .sqlArg("ids", Arrays.asList(1, 2, 3))
                .sqlArg("info","info")
                .pageIndex(1)
                .pagePerSize(2)
                .format();
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(st.getSql(), st.getSqlArgs());
        maps.forEach(System.out::println);
    }

}