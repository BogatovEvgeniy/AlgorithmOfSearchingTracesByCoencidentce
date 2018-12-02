package io.db;

import org.deckfour.xes.model.XEvent;

import java.sql.*;
import java.util.List;

public class DBWriter {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/Diss_DB?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "root";

    public static void init() {
        //STEP 2: Register JDBC driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void insertEvents(XEvent ... events) throws SQLException {

        //STEP 3: Open a connection
        System.out.println("Connecting to database...");
        Connection conn = getConnection();
        Savepoint attributeKeysInsertion = null;

        if (conn == null){
            return;
        }

        try {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            for (XEvent event : events) {
                for (String key : event.getAttributes().keySet()) {
                    String insertEventQuery = "REPLACE INTO `diss_db`.`attributes` (`key`) VALUES ("+ key + ");";
                    stmt.addBatch(insertEventQuery);
                }
            }
            stmt.executeBatch();
            attributeKeysInsertion = conn.setSavepoint("Attribute_keys_insertion");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback(attributeKeysInsertion);
        }
    }

    private static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
