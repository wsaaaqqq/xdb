package org.xht.xdb.springboot.starter;

import xht.xdb.Xdb;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SqlToolTest {

    @Test
    void format() {
        Xdb.sql("select * from xxx where id in (:ids)")
                .sqlArg("ids", Arrays.asList(1, 2, 3))
                .sqlArg("name", "n1")
                .format()
                .debug();
    }

    @Test
    void file() {
        Xdb.sqlFile("user/user_query.sql")
                .sqlArg("ids", Arrays.asList(1, 2, 3))
//                .sqlArg("name","n1")
                .format()
                .debug();
    }
}