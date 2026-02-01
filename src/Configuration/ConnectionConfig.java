package Configuration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionConfig {

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String DB_FOLDER = "database";
    private static final String DB_NAME = "transportation.db";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found. Add sqlite-jdbc.jar to your project.", e);
        }
    }

    public static String getDatabasePath() {
        String base = System.getProperty("user.dir");
        File dir = new File(base, DB_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, DB_NAME).getAbsolutePath();
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:" + getDatabasePath();
        Connection conn = DriverManager.getConnection(url);
        initDatabase(conn);
        return conn;
    }

    private static void initDatabase(Connection conn) {
        try (Statement st = conn.createStatement()) {
            st.execute(
                "CREATE TABLE IF NOT EXISTS users ("
                + "u_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "email TEXT NOT NULL, "
                + "name TEXT NOT NULL, "
                + "password TEXT NOT NULL, "
                + "created_at TEXT DEFAULT CURRENT_TIMESTAMP)"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS routes ("
                + "v_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "v_type TEXT, "
                + "origin TEXT, "
                + "destination TEXT)"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS bookings ("
                + "b_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "passenger TEXT NOT NULL, "
                + "passenger_id INTEGER REFERENCES users (u_id), "
                + "v_type TEXT, "
                + "v_id INTEGER REFERENCES routes (v_id), "
                + "route TEXT NOT NULL, "
                + "seat TEXT, "
                + "status TEXT DEFAULT 'Pending', "
                + "date TEXT DEFAULT CURRENT_TIMESTAMP)"
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database tables.", e);
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) conn.close();
            } catch (SQLException ignored) { }
        }
    }
}
