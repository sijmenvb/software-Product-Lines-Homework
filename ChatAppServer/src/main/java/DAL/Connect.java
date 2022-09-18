package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author sqlitetutorial.net
 * https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
 */
public class Connect {
	/**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    public Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\chatapp.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
