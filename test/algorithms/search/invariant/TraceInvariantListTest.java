package algorithms.search.invariant;

import algorithms.removal.TraceDuplicatesRemovingAlgorithm;
import io.FileUtils;
import io.ILogWriter;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.junit.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class TraceInvariantListTest {

    private static XLog log;
    private TraceInvariantList invariantTree;

    private static String ATTR_KEY_1 = "attr1";
    private static String ATTR_KEY_2 = "attr2";
    private static String ATTR_KEY_3 = "attr3";
    private static String ATTR_KEY_4 = "attr4";

    private static int ATTR_TEST_VAL_INT = 1;
    private static String ATTR_TEST_VAL_TIME = "1970-01-01T00:00:00.000+01:00";
    private static float ATTR_TEST_VAL_FLOAT = 1.1F;

    @Before
    public void initEnvironment() {
        invariantTree = new TraceInvariantList();
    }

    @BeforeClass
    public static void initClassValues() {
        File testLog = new File(FileUtils.getCurrentDirectoryPath() + "TestLog_4unique_traces_each_duplicated_twice.xes");
        try {
            FileUtils.createFileIfNeed(testLog);
            XesXmlParser xUniversalParser = new XesXmlParser();
            List<XLog> parse = xUniversalParser.parse(testLog);
            if (parse == null || parse.size() == 0) {
                assert false;
            }

            log = parse.get(0);

        } catch (Exception e) {
            assert false;
        }

    }

    @Test
    public void addInvariantNode() {
        TraceInvariantList.Node node = new TraceInvariantList.Node(ATTR_KEY_1);
        List<String> valuesList = new LinkedList<>();
        String eventVal = log.get(0).get(0).getAttributes().get(ATTR_KEY_1).toString();
        valuesList.add(eventVal);
        node.addTrace(valuesList);
        invariantTree.addInvariantNode(ATTR_KEY_1, node);

        TraceInvariantList.Node invariantNodeForKey = invariantTree.getInvariantNodeForKey(ATTR_KEY_1);
        assertNotNull(invariantNodeForKey);
        assertTrue(invariantNodeForKey.getAllAvailableValues().size() > 0);
        assertEquals(eventVal, invariantNodeForKey.getAllAvailableValues().get(0).get(0));
    }

    @Test
    public void addValue() {
    }

    @Test
    public void addInvariant() {
    }

    @Test
    public void insertOrReplaceInvariant() {
    }

    @Test
    public void nodesPerKey() {
    }

    @Test
    public void getInvariantNodeForKey() {
    }

    @Test
    public void clear() {
    }

    @Test
    public void size() {
    }

    @After
    public void clearEnvironment() {
        invariantTree = null;
    }

    @AfterClass
    public static void removeClassValues() {
        log = null;
    }
}