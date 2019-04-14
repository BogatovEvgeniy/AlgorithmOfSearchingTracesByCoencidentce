package io.db;

import algorithms.search.trace.AttributeSetWeightPerRanges;
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


    // AttrSets table
    private static final String TABLE_ATTR_SETS = "attribute_sets";
    private static final String ATTR_SETS_SET_ID = "attribute_set_id";
    private static final String ATTR_SETS_ATTR_NAME = "attribute_name";

    // ValueSets tab1le
    private static final String TABLE_VALUE_SETS = "value_sets";
    private static final String VALUE_SET_NUMBER = "value_set_num";
    private static final String VALUE_SET_KEY = "attr_key";
    private static final String VALUE_SET_NAME = "attr_value";

    //WeightResultsTable
    private static final String TABLE_WEIGHT_RESILTS = "weights_table";
    private static final String WEIGHTS_TABLE_ID = "id";
    private static final String WEIGHTS_RANGE_NUM = "range_num";
    private static final String WEIGHTS_ATTR_SET_NUM = "attr_set_num";
    private static final String WEIGHTS_VAL_SET_NUM = "val_set_num";
    private static final String WEIGHTS_WEIGHT = "weight";
    private static final String WEIGHTS_SUMMARY_WEIGHT = "summary_weight";
    public static final int FIRST_INDEX_VALUE = 0;
    private static Connection connection;

    public static DBWriter init() {
        connection = getConnection();
        //STEP 2: Register JDBC driver
        truncateTables();
        return new DBWriter();
    }

    private static void truncateTables() {
        try {
            Class.forName(JDBC_DRIVER);
            connection.createStatement().execute("SET FOREIGN_KEY_CHECKS=0;");

            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT Concat('TRUNCATE TABLE ',table_schema,'.',TABLE_NAME, ';')" +
                            " FROM INFORMATION_SCHEMA.TABLES where  table_schema in (" + DISS_DB + ");");

            while (resultSet.next()) {
                connection.createStatement().execute(resultSet.getString(1));
            }
            connection.createStatement().execute("SET FOREIGN_KEY_CHECKS=1;");

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
        forEachAttributeInEvent(getAttributeInsertionConsumerFunc(connection), firstEvent, secondEvent);
        insertEventValueInAttributeEventsTable(connection, rangeNum, attrSetIndex, attributeSet, firstEvent, firstEventIndex);
        insertEventValueInAttributeEventsTable(connection, rangeNum, attrSetIndex, attributeSet, secondEvent, secondEventIndex);
    }

    private Consumer<String> putEventInEventAttributesTableConsumer(Connection connection, XEvent event) {
        return key -> {
            Map<String, Integer> attributeIds = new HashMap<>();
            try {
                attributeIds.put(key, getAttributeIdByName(connection, key));

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

    private Consumer<String> getAttributeInsertionConsumerFunc(Connection connection) {
        return key -> {
            try {
                Statement stmt = connection.createStatement();
                String selectAttributeQuery = "SELECT * FROM " + DISS_DB_ATTRIBUTES + " WHERE name = '" + key + "';";
                ResultSet resultSet = stmt.executeQuery(selectAttributeQuery);
                if (!resultSet.next()) {
                    String insertAttributeQuery = "INSERT INTO " + DISS_DB_ATTRIBUTES + " VALUES ('" + key + "');";
                    stmt.executeUpdate(insertAttributeQuery);
                }
                resultSet.close();
                stmt.close();
            } catch (SQLException e) {
                try {
                    connection.rollback();
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
    private void insertEventValueInAttributeEventsTable(Connection connection, int rangeNum, int attrSetIndex, List<String> attributeSet, XEvent event, int eventIndex) {
        try {
            insertEventIfNotExist(connection, eventIndex);

            List<Integer> eventIds = getEventsForPerRangeAndAttrSet(connection, event, rangeNum, attrSetIndex);
            if (!eventIds.contains(eventIndex)) {
                for (String key : attributeSet) {
                    String query = "INSERT INTO " + TABLE_EVENT_ATTRIBUTES + " (range_num, attr_set_index, eventId, attribute_key, attribute_val)" + " " +
                            "VALUES (" + rangeNum + "," + attrSetIndex + "," + eventIndex + ",'" + key + "', '" + event.getAttributes().get(key) + "');";
                    connection.createStatement().executeUpdate(query);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> getEventsForPerRangeAndAttrSet(Connection connection, XEvent event, int rangeNum, int attrSetIndex) throws SQLException {
        String sql = "SELECT eventId FROM " + TABLE_EVENT_ATTRIBUTES +
                " WHERE range_num = " + rangeNum +
                " AND attr_set_index=" + attrSetIndex;
        ResultSet resultSet = connection.createStatement().executeQuery(sql);


        List<Integer> eventIds = new LinkedList<>();
        while (resultSet.next()) {
            eventIds.add(resultSet.getInt(1));
        }

        resultSet.close();
        return eventIds;
    }


    private void insertEventIfNotExist(Connection connection, int eventIndex) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT MAX(id) FROM " + TABLE_EVENT + ";");
        int lastInsertedEvent = resultSet.next() ? resultSet.getInt(ROW_SET_FIRST_COLUMN_INDEX) : NON_DEFINED_ID;
        if (lastInsertedEvent == ROW_SET_EMPTY_INDEX || eventIndex != lastInsertedEvent) {
            connection.createStatement().executeUpdate(getInsertOrSkipIfExits(eventIndex));
        }
        resultSet.close();
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
        int result;
        if (resultSet.next()) {
            result = resultSet.getInt(EVENT_ATTRIBUTES_EVENT_ID);
        } else {
            result = NON_DEFINED_ID;
        }
        resultSet.close();
        statement.close();
        return result;
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

    private static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public List<XEvent> getEventsPerAttrSet(int attrSetIndex, int rangeId) {
        List<XEvent> eventsPerAttributeSet = new LinkedList<>();
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
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventsPerAttributeSet;
    }

    public List<String> getAttrsPerAttrSet(int attrSetIndex) throws SQLException {
        List<String> keys = new LinkedList<>();
        try {
            ResultSet resultSet = getConnection().createStatement().executeQuery("SELECT " + EVENT_ATTRIBUTES_ATTRIBUTE_KEY + " FROM " + TABLE_EVENT_ATTRIBUTES + " WHERE " + EVENT_ATTRIBUTES_ATTR_SET_INDEX + "=" + attrSetIndex + " GROUP  BY " + EVENT_ATTRIBUTES_ATTRIBUTE_KEY);
            while (resultSet.next()) {
                keys.add(resultSet.getString(EVENT_ATTRIBUTES_ATTRIBUTE_KEY));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return keys;
    }

    public List<XEvent> getValueSetsPerAttrSet(int attrSetIndex, List<String> attributeKeys, int fromRangeIndex, int toRangeIndex) throws SQLException {
        List<XEvent> eventsPerAttributeSet = new LinkedList<>();

        if (fromRangeIndex > toRangeIndex) {
            throw new IllegalArgumentException("FROM  bigger than TO. Range indexes");
        }

        try {
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT * FROM " + TABLE_EVENT_ATTRIBUTES +
                            " WHERE " + EVENT_ATTRIBUTES_ATTR_SET_INDEX + "=" + attrSetIndex);

            int lastEventId = -1;
            XEventImpl currEvent = null;
            while (resultSet.next()) {
                int curEventId = resultSet.getInt(EVENT_ATTRIBUTES_EVENT_ID);

                if (lastEventId < 0 || lastEventId != curEventId) {
                    if (!AttributeUtils.containsDuplicate(eventsPerAttributeSet, currEvent) ||
                            (eventsPerAttributeSet.size() == 0 && currEvent != null)) {
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
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventsPerAttributeSet;
    }

    public void storeAttributeSets(List<List<String>> attributeSets) {
        try {
            String[] attributes = attributeSets.get(FIRST_INDEX_VALUE).toArray(new String[]{});
            String insertIntoPrefix = "INSERT INTO " + TABLE_ATTR_SETS + "(attribute_set_id, attribute_name)";
            String insertQuery = insertIntoPrefix + " VALUES (" + FIRST_INDEX_VALUE + ", '" + attributes[FIRST_INDEX_VALUE] + "')";
            connection.createStatement().executeUpdate(insertQuery);
            for (int attrIndex = 1; attrIndex < attributes.length; attrIndex++) {
                connection.createStatement().execute(insertIntoPrefix + " VALUES (" + FIRST_INDEX_VALUE + ", '" + attributes[attrIndex] + "')");
            }

            for (int attrSet = 1; attrSet < attributeSets.size(); attrSet++) {
                for (int attrIndex = 0; attrIndex < attributeSets.get(attrSet).size(); attrIndex++) {
                    connection.createStatement().execute(insertIntoPrefix + " VALUES (" + attrSet + ", '" + attributeSets.get(attrSet).get(attrIndex) + "')");
                }
            }
        } catch (
                SQLException ex)

        {
            ex.printStackTrace();
        }
    }

    public void storeValueSets(int attrSetIndex, List<XEvent> valueSetsPerAttr) {
        try {
            int lastEventSetNum = -1;

            if (attrSetIndex > 0) {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT MAX(value_set_num) FROM value_sets");
                if (resultSet.next()) {
                    lastEventSetNum = resultSet.getInt(1);
                }
                resultSet.close();
            }

            for (int eventIndex = 0; eventIndex < valueSetsPerAttr.size(); eventIndex++) {
                lastEventSetNum++;
                XAttributeMap attributes = valueSetsPerAttr.get(eventIndex).getAttributes();
                for (String key : attributes.keySet()) {
                    String insertQuery = "INSERT INTO " + TABLE_VALUE_SETS + "(value_set_num, attr_set_id, attr_key, attr_value)" + " VALUES ("
                            + lastEventSetNum
                            + ", " + attrSetIndex
                            + ", '" + key
                            + "', '" + attributes.get(key)
                            + "')";
                    connection.createStatement().execute(insertQuery);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void storeWeightCalculations(int attrSetIndex, AttributeSetWeightPerRanges weightPerRanges) {
        try {
            Map<Integer, Float> rangeIndexes = weightPerRanges.getRangeIndexes();
            for (Integer rangeNum : rangeIndexes.keySet()) {
                StringBuilder insertQuery = new StringBuilder("INSERT INTO weights_table(range_num, weight, attr_set, value_set) VALUES(");
                insertQuery.append(rangeNum);
                insertQuery.append(",");
                insertQuery.append(rangeIndexes.get(rangeNum));
                insertQuery.append(",'");
                insertQuery.append(attrSetIndex);
                insertQuery.append("','");
                insertQuery.append(weightPerRanges.getValues().values().toString());
                insertQuery.append("')");
                connection.createStatement().execute(insertQuery.toString());
            }

            StringBuilder insertQuery = new StringBuilder("INSERT INTO weights_table(attr_set, summary_weight) VALUES(");
            insertQuery.append(attrSetIndex);
            insertQuery.append(",");
            insertQuery.append(weightPerRanges.getWeight());
            insertQuery.append(")");
            connection.createStatement().execute(insertQuery.toString());

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Integer> getRangeSetPerValueSet(int attrSetIndex, XAttributeMap eventAttributes, List<String> attributes) {
        List<Integer> ranges = new LinkedList<>();
        try {
            StringBuilder selectQuery = new StringBuilder("SELECT range_num FROM ");
            selectQuery.append("(SELECT " + EVENT_ATTRIBUTES_RANGE_NUM + " FROM " + TABLE_EVENT_ATTRIBUTES);
            selectQuery.append(" WHERE " + EVENT_ATTRIBUTES_ATTR_SET_INDEX);
            selectQuery.append("=");
            selectQuery.append(attrSetIndex);
            selectQuery.append(" AND ");

            // Travers only through attributes in the attribute set
            selectQuery.append(EVENT_ATTRIBUTES_ATTRIBUTE_KEY);
            selectQuery.append("='");
            selectQuery.append(attributes.get(0));
            selectQuery.append("'");
            selectQuery.append(" AND ");
            selectQuery.append(EVENT_ATTRIBUTES_ATTRIBUTE_VAL);
            selectQuery.append("='");
            selectQuery.append(eventAttributes.get(attributes.get(0)));
            selectQuery.append("'");

            if (attributes.size() > 1) {
                selectQuery.append(" AND ");
                selectQuery.append(EVENT_ATTRIBUTES_RANGE_NUM);
                selectQuery.append(" IN (");
                selectQuery.append(wrapKeyValuesInRangeSubQueries(attrSetIndex, eventAttributes, attributes, 1));
                selectQuery.append(")");
            }


            selectQuery.append(") AS alias GROUP BY range_num");
            ResultSet resultSet = connection.createStatement().executeQuery(selectQuery.toString());
            while (resultSet.next()) {
                ranges.add(resultSet.getInt(EVENT_ATTRIBUTES_RANGE_NUM));
            }
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ranges;
    }

    private String wrapKeyValuesInRangeSubQueries(int attrSetIndex, XAttributeMap eventAttributes, List<String> attributes, int attributeIndex) {

        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT " + EVENT_ATTRIBUTES_RANGE_NUM + " FROM " + TABLE_EVENT_ATTRIBUTES);
        selectQuery.append(" WHERE " + EVENT_ATTRIBUTES_ATTR_SET_INDEX);
        selectQuery.append("=");
        selectQuery.append(attrSetIndex);
        selectQuery.append(" AND ");

        // Travers only through attributes in the attribute set
        selectQuery.append(EVENT_ATTRIBUTES_ATTRIBUTE_KEY);
        selectQuery.append("='");
        selectQuery.append(attributes.get(attributeIndex));
        selectQuery.append("'");
        selectQuery.append(" AND ");
        selectQuery.append(EVENT_ATTRIBUTES_ATTRIBUTE_VAL);
        selectQuery.append("='");
        selectQuery.append(eventAttributes.get(attributes.get(attributeIndex)));
        selectQuery.append("'");
        if (attributeIndex < attributes.size() - 1) {
            selectQuery.append(" AND ");
            selectQuery.append(EVENT_ATTRIBUTES_RANGE_NUM);
            selectQuery.append(" IN (");
            selectQuery.append(wrapKeyValuesInRangeSubQueries(attrSetIndex, eventAttributes, attributes, ++attributeIndex));
            selectQuery.append(")");
        }
        return selectQuery.toString();
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
