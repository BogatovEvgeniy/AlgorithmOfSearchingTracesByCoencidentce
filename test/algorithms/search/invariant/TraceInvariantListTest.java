//package algorithms.search.invariant;
//
//import algorithms.search.trace.locator.invariant.Node;
//import algorithms.search.trace.locator.invariant.TraceInvariantList;
//import base.LogTestBaseClass;
//import exceptions.InvariantAlreadyExistsException;
//import org.deckfour.xes.model.XLog;
//import org.junit.*;
//
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class TraceInvariantListTest extends LogTestBaseClass {
//
//    private static XLog log;
//    private TraceInvariantList invariantTree;
//
//    private static String ATTR_KEY_1 = "attr1";
//    private static String ATTR_KEY_2 = "attr2";
//    private static String ATTR_KEY_3 = "attr3";
//    private static String ATTR_KEY_4 = "attr4";
//
//    private static int ATTR_TEST_VAL_INT = 1;
//    private static String ATTR_TEST_VAL_TIME = "1970-01-01T00:00:00.000+01:00";
//    private static float ATTR_TEST_VAL_FLOAT = 1.1F;
//
//    @BeforeClass
//    public static void initClassValues() {
//        log = getLogInstance();
//
//    }
//
//    @Before
//    public void initEnvironment() {
//        invariantTree = new TraceInvariantList();
//    }
//
//    @Test
//    public void addInvariantNode() {
//        Node node = new Node(ATTR_KEY_1);
//        List<String> valuesList = new LinkedList<>();
//        String eventVal = log.get(0).get(0).getAttributes().get(ATTR_KEY_1).toString();
//        valuesList.add(eventVal);
//        node.addTrace(valuesList);
//        invariantTree.addInvariantNode(node);
//
//        Node invariantNodeForKey = invariantTree.getEventsRuleSetPerKey(ATTR_KEY_1);
//        assertNotNull(invariantNodeForKey);
//        assertTrue(invariantNodeForKey.getAllAvailableValues().size() > 0);
//        assertEquals(eventVal, invariantNodeForKey.getAllAvailableValues().get(0).get(0));
//    }
//
//    @Test
//    public void insertOrReplaceInvariantAddNewNode() {
//        String[] invariantListTestData = {"A", "B", "C", "D"};
//        List<String> invariantList = new LinkedList<>();
//        invariantList.addAll(Arrays.asList(invariantListTestData));
//        invariantTree.insertOrReplaceInvariant(ATTR_KEY_1, invariantList);
//
//        Node invariantNodeForKey = invariantTree.getEventsRuleSetPerKey(ATTR_KEY_1);
//        List<String> attributeInvariant = invariantNodeForKey.getAttributeInvariant();
//
//        assertNotNull(invariantNodeForKey);
//        assertTrue(attributeInvariant.size() == invariantListTestData.length);
//        assertTrue(attributeInvariant.containsAll(invariantList));
//    }
//
//    @Test
//    public void insertOrReplaceInvariantReplaceNode() throws InvariantAlreadyExistsException {
//        String[] invariantTestData = {"A", "B", "C", "D"};
//        String[] invariantTestDataForReplace = {"A", "B", "C", "D", "E", "F"};
//        List<String> invariantList = new LinkedList<>();
//        invariantList.addAll(Arrays.asList(invariantTestData));
//        invariantTree.insertOrReplaceInvariant(ATTR_KEY_1, invariantList);
//
//        Node invariantNodeForKey = invariantTree.getEventsRuleSetPerKey(ATTR_KEY_1);
//        List<String> attributeInvariant = invariantNodeForKey.getAttributeInvariant();
//        assertNotNull(invariantNodeForKey);
//        assertTrue(attributeInvariant.size() == invariantTestData.length);
//
//        List<String> invariantListForReplace = new LinkedList<>();
//        invariantListForReplace.addAll(Arrays.asList(invariantTestDataForReplace));
//        invariantTree.insertOrReplaceInvariant(ATTR_KEY_1, invariantListForReplace);
//        invariantNodeForKey = invariantTree.getEventsRuleSetPerKey(ATTR_KEY_1);
//        attributeInvariant = invariantNodeForKey.getAttributeInvariant();
//
//        assertNotNull(invariantNodeForKey);
//        assertTrue(attributeInvariant.size() != invariantTestData.length);
//        assertTrue(attributeInvariant.size() == invariantTestDataForReplace.length);
//        assertTrue(attributeInvariant.containsAll(invariantListForReplace));
//
//    }
//
//
//    @Test
//    public void getInvariantNodeForKey() {
//        Node node = new Node(ATTR_KEY_1);
//        List<String> valuesList = new LinkedList<>();
//        String eventVal = log.get(0).get(0).getAttributes().get(ATTR_KEY_1).toString();
//        valuesList.add(eventVal);
//        node.addTrace(valuesList);
//        invariantTree.addInvariantNode(node);
//
//        Node invariantNodeForKey = invariantTree.getEventsRuleSetPerKey(ATTR_KEY_1);
//        assertNotNull(invariantNodeForKey);
//        assertTrue(invariantNodeForKey.equals(node));
//    }
//
//    @Test
//    public void clear() {
//        TraceInvariantList list = new TraceInvariantList();
//        Node node1 = new Node(ATTR_KEY_1);
//        Node node2 = new Node(ATTR_KEY_2);
//        list.addInvariantNode(node1);
//        list.addInvariantNode(node2);
//        list.clear();
//
//        assertNotNull(list);
//        assertTrue(list.size() == 0);
//    }
//
//    @Test
//    public void sizeIsNull() {
//        TraceInvariantList list = new TraceInvariantList();
//        assertTrue(list.size() == 0);
//    }
//
//    @Test
//    public void sizeIsTwo() {
//        TraceInvariantList list = new TraceInvariantList();
//        Node node1 = new Node(ATTR_KEY_1);
//        Node node2 = new Node(ATTR_KEY_2);
////        list.addInvariantNode(node1);
////        list.addInvariantNode(node2);
//
//        assertNotNull(list);
//        assertTrue(list.size() == 2);
//    }
//
//    @After
//    public void clearEnvironment() {
//        invariantTree = null;
//    }
//
//    @AfterClass
//    public static void removeClassValues() {
//        log = null;
//    }
//}