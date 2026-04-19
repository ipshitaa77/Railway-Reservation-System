package Railways;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB_Connection {
    public static Connection create_dbconnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/",
            "root",
            "admin"
        );
    }

    public static Connection create_connection(String db_name) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/" + db_name,
            "root",
            "admin"
        );
    }
}