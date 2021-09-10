package org.xht.xdb.springboot.starter;

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
    void contextLoads() {
        SqlTool st = Xdb.sqlFile("123")
                .sqlArg("code", Arrays.asList(6,7))
                .sqlArg("code_full", "GL101B")
                .pageIndex(0)
                .pagePerSize(5)
                .format();
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(st.getSql(), st.getSqlArgs());
        maps.forEach(System.out::println);
    }

}
