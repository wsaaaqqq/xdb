import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import org.sqlite.javax.SQLitePooledConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlitePoolDataSource extends SQLiteConnectionPoolDataSource implements DataSource {

    @Override
    public Connection getConnection() throws SQLException {
        SQLitePooledConnection pooledConnection;
        try {
            pooledConnection = (SQLitePooledConnection) super.getPooledConnection();
        } catch (Exception e) {
            pooledConnection = null;
        }
        return pooledConnection.getConnection();
    }
}
