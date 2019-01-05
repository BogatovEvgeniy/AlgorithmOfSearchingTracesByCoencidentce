package io.db;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import utils.AttributeUtils;

import java.sql.*;
import java.util.*;
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
    private static final String TABLE_EVENT = "events";

    // EventAttributes Table
    private static final String TABLE_EVENT_ATTRIBUTES = "event_attributes";
    private static final String EVENT_ATTRIBUTES_RANGE_NUM = "range_num";
    private static final String EVENT_ATTRIBUTES_ATTR_SET_INDEX = "attr_set_index";
    private static final String EVENT_ATTRIBUTES_EVENT_ID = "eventId";
    private static final String EVENT_ATTRIBUTES_ATTRIBUTE_KEY = "attribute_key";
    private static final String EVENT_ATTRIBUTES_ATTRIBUTE_VAL = "attribute_val";
    public static final int ROW_SET_FIRST_COLUMN_INDEX = 1;
    public static final int ROW_SET_EMPTY_INDEX = 0;
    public static final String DISS_DB = "'diss_db'";

    public static DBWriter init() {
        //STEP 2: Register JDBC driver
        truncateTables();
        return new DBWriter();
    }

    private static void truncateTables() {
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = getConnection();
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS=0;");

            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT Concat('TRUNCATE TABLE ',table_schema,'.',TABLE_NAME, ';')" +
                            " FROM INFORMATION_SCHEMA.TABLES where  table_schema in (" + DISS_DB + ");");

            while (resultSet.next()) {
                conn.createStatement().execute(resultSet.getString(1));
            }
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS=1;");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPairOfEvents(List<String> attributeSet,
                                   int rangeNum,
                                   int attrSetIndex,
                                   int firstEventIndex,
                                   int secondEventIndex,
                                   XEvent firstEvent,
                                   XEvent secondEvent) {

        //STEP 3: Open a connection
        Connection conn = null;
        try {
            conn = getConnection();
            forEachAttributeInEvent(getAttributeInsertionConsumerFunc(conn), firstEvent, secondEvent);
            insertEventValueInAttributeEventsTable(conn, rangeNum, attrSetIndex, attributeSet, firstEvent, firstEventIndex);
            insertEventValueInAttributeEventsTable(conn, rangeNum, attrSetIndex, attributeSet, secondEvent, secondEventIndex);
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
    private void insertEventValueInAttributeEventsTable(Connection conn, int rangeNum, int attrSetIndex, List<String> attributeSet, XEvent event, int eventIndex) {
        try {
            insertEventIfNotExist(conn, eventIndex);

            List<Integer> eventIds = getEventsForPerRangeAndAttrSet(conn, event, rangeNum, attrSetIndex);
            if (!eventIds.contains(eventIndex)) {
                for (String key : attributeSet) {
                    conn.createStatement().executeUpdate("INSERT INTO " + TABLE_EVENT_ATTRIBUTES + " (range_num, attr_set_index, eventId, attribute_key, attribute_val) " +
                            "VALUES (" + rangeNum + "," + attrSetIndex + "," + eventIndex + ",'" + key + "', '" + event.getAttributes().get(key) + "');");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> getEventsForPerRangeAndAttrSet(Connection conn, XEvent event, int rangeNum, int attrSetIndex) throws SQLException {
        String sql = "SELECT eventId FROM " + TABLE_EVENT_ATTRIBUTES +
                " WHERE range_num = " + rangeNum +
                " AND attr_set_index=" + attrSetIndex;
        ResultSet resultSet = conn.createStatement().executeQuery(sql);


        List<Integer> eventIds = new LinkedList<>();
        while (resultSet.next()) {
            eventIds.add(resultSet.getInt(1));
        }

        return eventIds;
    }


    private void insertEventIfNotExist(Connection conn, int eventIndex) throws SQLException {
        ResultSet resultSet = conn.createStatement().executeQuery("SELECT MAX(id) FROM " + TABLE_EVENT + ";");
        int lastInsertedEvent = resultSet.next() ? resultSet.getInt(ROW_SET_FIRST_COLUMN_INDEX) : NON_DEFINED_ID;
        if (lastInsertedEvent == ROW_SET_EMPTY_INDEX || eventIndex != lastInsertedEvent) {
            conn.createStatement().executeUpdate(getInsertOrSkipIfExits(eventIndex));
        }
    }

    private String getInsertOrSkipIfExits(int eventIndex) {
        return "INSERT INTO events (id)\n" +
                "SELECT * FROM (SELECT '" + eventIndex + "') AS tmp\n" +
                "WHERE NOT EXISTS (\n" +
                "    SELECT id FROM events WHERE id = '" + eventIndex + "'\n" +
                ") LIMIT 1;";
    }

    private int getEventIdByAttributes(Connection conn, XEvent event, int rangeNum, int attrSetIndex) throws SQLException {
        Statement statement = conn.createStatement();
        String selectEventByAttributeQuery = getSelectionQueryEventIdWithAttributes(event, rangeNum, attrSetIndex);
        ResultSet resultSet = statement.executeQuery(selectEventByAttributeQuery);
        if (resultSet.next()) {
            return resultSet.getInt(EVENT_ATTRIBUTES_EVENT_ID);
        } else {
            return NON_DEFINED_ID;
        }
    }

    private String getSelectionQueryEventIdWithAttributes(XEvent event, int rangeNum, int attrSetIndex) {
        StringBuilder selectEventByAttributeQuery = new StringBuilder("SELECT eventId FROM " + TABLE_EVENT_ATTRIBUTES + " WHERE ");

        XAttributeMap attributes = event.getAttributes();
        Iterator<String> keys = attributes.keySet().iterator();
        if (keys.hasNext()) {
            String key = keys.next();
            selectEventByAttributeQuery.append(EVENT_ATTRIBUTES_RANGE_NUM);
            selectEventByAttributeQuery.append("=");
            selectEventByAttributeQuery.append("'" + rangeNum + "'");
            selectEventByAttributeQuery.append(" AND ");
            selectEventByAttributeQuery.append(EVENT_ATTRIBUTES_ATTR_SET_INDEX);
            selectEventByAttributeQuery.append("=");
            selectEventByAttributeQuery.append("'" + attrSetIndex + "'");
            selectEventByAttributeQuery.append(" AND ");
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

    public List<XEvent> getEventsPerAttrSet(int attrSetIndex, int rangeId) throws SQLException {
        List<XEvent> eventsPerAttributeSet = new LinkedList<>();
        Connection connection = getConnection();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT * FROM " + TABLE_EVENT_ATTRIBUTES +
                            " WHERE " + EVENT_ATTRIBUTES_ATTR_SET_INDEX + "=" + attrSetIndex +
                            " AND " + EVENT_ATTRIBUTES_RANGE_NUM + "=" + rangeId);

            int lastEventId = -1;
            XEventImpl currEvent = null;
            while (resultSet.next()) {
                int curEventId = resultSet.getInt(EVENT_ATTRIBUTES_EVENT_ID);

                if (lastEventId < 0 || lastEventId != curEventId) {
                    currEvent = new XEventImpl();
                    eventsPerAttributeSet.add(currEvent);
                }

                lastEventId = curEventId;
                XAttributeMap attributes = currEvent.getAttributes();
                String key = resultSet.getString(EVENT_ATTRIBUTES_ATTRIBUTE_KEY);
                XAttribute attrVal = new XAttributeLiteralImpl(key, resultSet.getString(EVENT_ATTRIBUTES_ATTRIBUTE_VAL));
                attributes.put(key, attrVal);
            }
        } finally {
            connection.close();
        }

        return eventsPerAttributeSet;
    }

    public List<String> getAttrsPerAttrSet(int attrSetIndex) throws SQLException {
        List<String> keys = new LinkedList<>();
        Connection connection = getConnection();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT " + EVENT_ATTRIBUTES_ATTRIBUTE_KEY + " FROM " + TABLE_EVENT_ATTRIBUTES + " WHERE " + EVENT_ATTRIBUTES_ATTR_SET_INDEX + "=" + attrSetIndex + " GROUP  BY " + EVENT_ATTRIBUTES_ATTRIBUTE_KEY);
            while (resultSet.next()) {
                keys.add(resultSet.getString(EVENT_ATTRIBUTES_ATTRIBUTE_KEY));
            }
        } finally {
            connection.close();
        }

        return keys;
    }

    public List<XEvent> getValueSetsPerAttrSet(int attrSetIndex, List<String> attributeKeys, int fromRangeIndex, int toRangeIndex) throws SQLException {
        List<XEvent> eventsPerAttributeSet = new LinkedList<>();

        if (fromRangeIndex > toRangeIndex) {
            throw new IllegalArgumentException("FROM  bigger than TO. Range indexes");
        }

        Connection connection = getConnection();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT * FROM " + TABLE_EVENT_ATTRIBUTES +
                            " WHERE " + EVENT_ATTRIBUTES_ATTR_SET_INDEX + "=" + attrSetIndex);

            int lastEventId = -1;
            XEventImpl currEvent = null;
            while (resultSet.next()) {
                int curEventId = resultSet.getInt(EVENT_ATTRIBUTES_EVENT_ID);

                if (lastEventId < 0 || lastEventId != curEventId) {
                    if(AttributeUtils.isKeyValSetAbsent(eventsPerAttributeSet, currEvent) ||
                            (eventsPerAttributeSet.size() == 0 && currEvent != null)){
                        eventsPerAttributeSet.add(currEvent);
                    }

                    currEvent = new XEventImpl();
                }

                lastEventId = curEventId;
                XAttributeMap currEventAttr = currEvent.getAttributes();
                String key = resultSet.getString(EVENT_ATTRIBUTES_ATTRIBUTE_KEY);
                XAttribute attrVal = new XAttributeLiteralImpl(key, resultSet.getString(EVENT_ATTRIBUTES_ATTRIBUTE_VAL));
                if (attributeKeys.contains(key)) {
                    currEventAttr.put(key, attrVal);
                }
            }
        } finally {
            connection.close();
        }
        return eventsPerAttributeSet;
    }
}
