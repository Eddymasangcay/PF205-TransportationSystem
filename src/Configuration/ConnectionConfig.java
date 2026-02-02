package Configuration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

    // Default starting IDs when tables are empty
    private static final int DEFAULT_USER_ID = 1;
    private static final int DEFAULT_VEHICLE_ID = 101;
    private static final int DEFAULT_BOOKING_ID = 1001;

    private static void initDatabase(Connection conn) {
        try (Statement st = conn.createStatement()) {
            st.execute(
                "CREATE TABLE IF NOT EXISTS users ("
                + "u_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "u_type TEXT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "email TEXT NOT NULL, "
                + "name TEXT NOT NULL, "
                + "password TEXT NOT NULL, "
                + "created_at TEXT DEFAULT CURRENT_TIMESTAMP)"
            );
            ensureColumn(st, "users", "u_type", "TEXT DEFAULT 'user'");
            st.execute(
                "CREATE TABLE IF NOT EXISTS routes ("
                + "v_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "v_type TEXT, "
                + "v_price INTEGER, "
                + "origin TEXT, "
                + "destination TEXT)"
            );
            ensureColumn(st, "routes", "v_price", "INTEGER");
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
            st.execute(
                "CREATE TABLE IF NOT EXISTS receipts ("
                + "r_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "u_id INTEGER REFERENCES users (u_id), "
                + "username TEXT, "
                + "b_id INTEGER UNIQUE REFERENCES bookings (b_id), "
                + "origin TEXT, "
                + "destination TEXT, "
                + "seat TEXT, "
                + "price INTEGER DEFAULT 0, "
                + "date TEXT, "
                + "created_at TEXT DEFAULT CURRENT_TIMESTAMP)"
            );
            ensureColumn(st, "receipts", "price", "INTEGER DEFAULT 0");
            ensureColumn(st, "receipts", "created_at", "TEXT DEFAULT CURRENT_TIMESTAMP");
            // Reset sequences to default values when tables are empty
            resetSequenceIfEmpty(st, "users", "u_id", DEFAULT_USER_ID);
            resetSequenceIfEmpty(st, "routes", "v_id", DEFAULT_VEHICLE_ID);
            resetSequenceIfEmpty(st, "bookings", "b_id", DEFAULT_BOOKING_ID);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database tables.", e);
        }
    }

    private static void ensureColumn(Statement st, String table, String column, String typeDef) {
        try (ResultSet rs = st.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) return;
            }
        } catch (SQLException e) { return; }
        try {
            st.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + typeDef);
        } catch (SQLException ignored) { }
    }

    private static void resetSequenceIfEmpty(Statement st, String tableName, String idColumn, int defaultStartId) throws SQLException {
        // Check if the table is empty
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Table is empty, reset the sequence to start at defaultStartId
                // SQLite stores sequence info in sqlite_sequence table
                // We set seq to (defaultStartId - 1) so the next insert gets defaultStartId
                st.execute("DELETE FROM sqlite_sequence WHERE name = '" + tableName + "'");
                st.execute("INSERT INTO sqlite_sequence (name, seq) VALUES ('" + tableName + "', " + (defaultStartId - 1) + ")");
            }
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
