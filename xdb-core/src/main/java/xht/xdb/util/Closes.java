package xht.xdb.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class Closes {

    public static void closeConnection(Connection conn) {
        if (conn!=null){
            try {
                if(!conn.isClosed()){
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("",e);
            }

        }
    }

}
