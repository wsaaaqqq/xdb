package org.xht.xdb.demo.controller;

import xht.xdb.Xdb;
import xht.xdb.sql.SqlTool;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class Test {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public Test(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    @RequestMapping("/t")
    public Object test(){
        SqlTool st = Xdb.sqlFile("123")
//                .sqlArg("SBMC", "A")
                .sqlArg("DYDJ_BM", Arrays.asList("33","08"))
//                .pageIndex(1)
//                .pagePerSize(5)
                .format();
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(st.getSql(), st.getSqlArgs());
        maps.forEach(System.out::println);
        return maps;
    }

    @RequestMapping("/t2")
    public Object test2(){
        return Xdb.sql("select * from t_web_sbgl_ecsbjb")
                .pageIndex(1)
                .pagePerSize(10)
                .executeQuery()
                .result();
    }
}
