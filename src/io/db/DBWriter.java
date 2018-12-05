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
    private static final String TABLE_EVENT_ATTRIBUTES = "event_attributes";
    private static final String EVENT_ATTRIBUTES_EVENT_ID = "eventId";
    private static final String EVENT_ATTRIBUTES_ATTRIBUTE_KEY = "attribute_key";
    private static final String EVENT_ATTRIBUTES_ATTRIBUTE_VAL = "attribute_val";

    public static DBWriter init() {
        //STEP 2: Register JDBC driver
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = getConnection();
            conn.createStatement().execute("DELETE FROM " + TABLE_EVENT_ATTRIBUTES);
            conn.createStatement().execute("DELETE FROM " + TABLE_ATTRIBUTE_NAME);
            conn.createStatement().execute("DELETE FROM " + TABLE_EVENT_NAME);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return new DBWriter();
    }

    public void insertPairOfEvents(int firstEventIndex, int secondComparisionValIndex, XEvent firstEvent, XEvent secondEvent) {

        //STEP 3: Open a connection
        System.out.println("Connecting to database...");
        Connection conn = null;
        try {
            conn = getConnection();
            forEachAttributeInEvent(getAttributeInsertionConsumerFunc(conn), firstEvent, secondEvent);
            insertEventValueInAttributeEventsTable(conn, firstEvent, firstEventIndex);
            insertEventValueInAttributeEventsTable(conn, secondEvent, secondComparisionValIndex);
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

                Statement stmt = conn.createStatement();
                String selectAttributeQuery = "SELECT * FROM " + DISS_DB_ATTRIBUTES + " WHERE name = '" + key + "';";
                ResultSet resultSet = stmt.executeQuery(selectAttributeQuery);
                if (!resultSet.next()) {
                    String insertAttributeQuery = "INSERT INTO " + DISS_DB_ATTRIBUTES + " VALUES ('" + key + "');";
                    stmt.executeUpdate(insertAttributeQuery);
                }
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
    private void insertEventValueInAttributeEventsTable(Connection conn, XEvent event, int eventIndex) {

//        try {
//            int eventId = getEventIdByAttributes(conn, event);
//            if (eventId == NON_DEFINED_ID) {
//                eventId = conn.createStatement().executeUpdate("INSERT INTO " + TABLE_EVENT_NAME + " VALUES (NULL);");
//                for (String key : event.getAttributes().keySet()) {
//                    conn.createStatement().executeUpdate("INSERT INTO " + TABLE_EVENT_ATTRIBUTES + " (eventId, attribute_key, attribute_val) " +
//                            "VALUES (" + eventId + ",'" + key + "', '" + event.getAttributes().get(key) + "');");
//                }
//            } else {
//                for (String key : event.getAttributes().keySet()) {
//                    conn.createStatement().executeUpdate("INSERT INTO " + TABLE_EVENT_ATTRIBUTES + " (eventId, attribute_key, attribute_val) " +
//                            "VALUES (" + (eventId + 1) + ",'" + key + "','" + event.getAttributes().get(key) + "')");
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

        try {
            int eventId = getEventIdByAttributes(conn, event);
            if (eventId == NON_DEFINED_ID) {
                conn.createStatement().executeUpdate("INSERT INTO " + TABLE_EVENT_NAME + " VALUES ('" + eventIndex + "');");
                for (String key : event.getAttributes().keySet()) {
                    conn.createStatement().executeUpdate("INSERT INTO " + TABLE_EVENT_ATTRIBUTES + " (eventId, attribute_key, attribute_val) " +
                            "VALUES (" + eventIndex + ",'" + key + "', '" + event.getAttributes().get(key) + "');");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getEventIdByAttributes(Connection conn, XEvent event) throws SQLException {
        Statement statement = conn.createStatement();
        String selectEventByAttributeQuery = getSelectionQueryEventIdWithAttributes(event);
        ResultSet resultSet = statement.executeQuery(selectEventByAttributeQuery);
        if (resultSet.next()) {
            return resultSet.getInt(EVENT_ATTRIBUTES_EVENT_ID);
        } else {
            return NON_DEFINED_ID;
        }
    }

    private String getSelectionQueryEventIdWithAttributes(XEvent event) {
        StringBuilder selectEventByAttributeQuery = new StringBuilder("SELECT eventId FROM " + TABLE_EVENT_ATTRIBUTES + " WHERE ");

        XAttributeMap attributes = event.getAttributes();
        Iterator<String> keys = attributes.keySet().iterator();
        if (keys.hasNext()) {
            String key = keys.next();
            selectEventByAttributeQuery.append(EVENT_ATTRIBUTES_ATTRIBUTE_KEY);
            selectEventByAttributeQuery.append("=");
            selectEventByAttributeQuery.append("'" + key + "'");
            selectEventByAttributeQuery.append(" AND ");
            selectEventByAttributeQuery.append(EVENT_ATTRIBUTES_ATTRIBUTE_VAL);
            selectEventByAttributeQuery.append("=");
            selectEventByAttributeQuery.append("'" + attributes.get(key) + "'");
        }

        while (keys.hasNext()) {
            String key = keys.next();
            selectEventByAttributeQuery.append(" UNION ");
            selectEventByAttributeQuery.append("SELECT eventId FROM " + TABLE_EVENT_ATTRIBUTES + " WHERE ");
            selectEventByAttributeQuery.append(EVENT_ATTRIBUTES_ATTRIBUTE_KEY);
            selectEventByAttributeQuery.append("=");
            selectEventByAttributeQuery.append("'" + key + "'");
            selectEventByAttributeQuery.append(" AND ");
            selectEventByAttributeQuery.append(EVENT_ATTRIBUTES_ATTRIBUTE_VAL);
            selectEventByAttributeQuery.append("=");
            selectEventByAttributeQuery.append("'" + attributes.get(key) + "'");
        }

        selectEventByAttributeQuery.append(" GROUP BY eventId");
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
