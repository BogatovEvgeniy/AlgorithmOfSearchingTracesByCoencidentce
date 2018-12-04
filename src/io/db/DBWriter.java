package io.db;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;


//TODO In a first implementation will be god object, then can be divided according SOLID principle
public class DBWriter {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/Diss_DB?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    public static final String DISS_DB_ATTRIBUTES = "`diss_db`.`attributes`";
    private static final int NON_DEFINED_ID = -1;

    // Database credentials
    static final String USER = "root";
    static final String PASS = "root";

    // Common values

    private static final String TABLE_ID = "id";

    // Attribute Table
    private static final String TABLE_ATTRIBUTE_NAME = "attributes";
    private static final String ATTRIBUTE_DB_ID = "ID";
    private static final String ATTRIBUTE_DB_KEY = "key";

    //Events Table
    private static final String TABLE_EVENT_NAME = "events";

    // EventAttributes Table
    private static final String TABLE_EVENT_ATTRIBUTES_TABLE = "event_attributes";

    public static DBWriter init() {
        //STEP 2: Register JDBC driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new DBWriter();
    }

    public void insertEvents(XEvent... events) {

        //STEP 3: Open a connection
        System.out.println("Connecting to database...");
        Connection conn = null;
        try {
            conn = getConnection();
            forEachAttributeInEvent(getAttributeInsertionConsumerFunc(conn), events);
            for (XEvent event : events) {
                putEventInEventAttributesTableConsumer(conn, event);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Consumer<String> putEventInEventAttributesTableConsumer(Connection conn, XEvent event) {
        return key -> {
            Map<String, Integer> attributeIds = new HashMap<>();
            try {
                attributeIds.put(key, getAttributeIdByName(conn, key));
                insertEventValueInAttributeEventsTable(conn, attributeIds, event);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }


    // -------------------- ATTRIBUTE METHODS ---------------------------------------------- //

    private int getAttributeIdByName(Connection conn, String attrKey) throws SQLException {
        ResultSet attributes = getAttributes(conn);
        while (attributes.next()) {
            if (attributes.getString(ATTRIBUTE_DB_KEY).equals(attrKey)) {
                return attributes.getInt(ATTRIBUTE_DB_ID);
            }
        }
        return NON_DEFINED_ID;
    }

    private Consumer<String> getAttributeInsertionConsumerFunc(Connection conn) {
        return key -> {
            try {

                conn.setAutoCommit(false);
                Statement stmt = conn.createStatement();
                String insertEventQuery = "REPLACE INTO " + DISS_DB_ATTRIBUTES + " (`key`) VALUES (" + key + ");";
                stmt.addBatch(insertEventQuery);
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        };
    }


    private ResultSet getAttributes(Connection conn) throws SQLException {
        String getAttributeKeysQuery = "SELECT * FROM " + DISS_DB_ATTRIBUTES;
        return conn.prepareStatement(getAttributeKeysQuery).executeQuery();
    }


    // ----------------------------------------- EventsAttributes Table ------------------------------------------ //
    private void insertEventValueInAttributeEventsTable(Connection conn, Map<String, Integer> attributeIds, XEvent event) throws SQLException {
        try {
            int eventId = getEventIdByAttributes(conn, event);
            if (eventId == NON_DEFINED_ID) {
                eventId = conn.createStatement().executeUpdate("INSERT INTO" + TABLE_EVENT_NAME + "VALUES (NULL);");
                for (String key : event.getAttributes().keySet()) {
                    conn.createStatement().executeUpdate("INSERT INTO" + TABLE_EVENT_ATTRIBUTES_TABLE + " (eventId, attributeId, attribute_val) " +
                            "VALUES (" + eventId + "," + attributeIds.get(key) + ");");
                }
            } else {
                for (String key : event.getAttributes().keySet()) {
                    conn.createStatement().executeUpdate("INSERT INTO" + TABLE_EVENT_ATTRIBUTES_TABLE + " (eventId, attributeId, attribute_val) " +
                            "VALUES (" + eventId + "," + attributeIds.get(key) + "," + event.getAttributes().get(key) + ")" +
                            " WHERE eventId=" + eventId);
                }
            }




        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    private int getEventIdByAttributes(Connection conn, XEvent event) throws SQLException {
        Statement statement = conn.createStatement();
        String selectEventByAttributeQuery = getSelectionQueryEventIdWithAttributes(event);
        ResultSet resultSet = statement.executeQuery(selectEventByAttributeQuery);
        if (resultSet.next()) {
            return resultSet.getInt(TABLE_ID);
        } else {
            return NON_DEFINED_ID;
        }
    }

    private String getSelectionQueryEventIdWithAttributes(XEvent event) {
        StringBuilder selectEventByAttributeQuery = new StringBuilder("SELECT id FROM diss_db.test WHERE ");

        XAttributeMap attributes = event.getAttributes();
        Iterator<String> keys = attributes.keySet().iterator();
        if (keys.hasNext()) {
            String key = keys.next();
            selectEventByAttributeQuery.append(key);
            selectEventByAttributeQuery.append("=");
            selectEventByAttributeQuery.append(attributes.get(key));
        }

        while (keys.hasNext()) {
            String key = keys.next();
            selectEventByAttributeQuery.append(" AND ");
            selectEventByAttributeQuery.append(key);
            selectEventByAttributeQuery.append("=");
            selectEventByAttributeQuery.append(attributes.get(key));
        }
        return selectEventByAttributeQuery.toString();
    }

    private void forEachAttributeInEvent(Consumer consumer, XEvent... events) {
        for (XEvent event : events) {
            for (String key : event.getAttributes().keySet()) {
                consumer.accept(key);
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
