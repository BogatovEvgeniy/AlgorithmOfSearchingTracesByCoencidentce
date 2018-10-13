package algorithms.search.invariant;

import algorithms.removal.TraceDuplicatesRemovingAlgorithm;
import io.FileUtils;
import io.ILogWriter;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class TraceInvariantListTest {

    private TraceInvariantList invariantTree;

    private static String ATTR_KEY_VAL_1 = "KEY_1";
    private static String ATTR_KEY_VAL_2 = "KEY_2";

    private static String ATTR_TEST_VAL_STRING_VAL_1 = "string val_1";
    private static String ATTR_TEST_VAL_STRING_VAL_2 = "string val_2";
    private static int ATTR_TEST_VAL_INT = 1;
    private static String ATTR_TEST_VAL_TIME = "1970-01-01T00:00:00.000+01:00";
    private static float ATTR_TEST_VAL_FLOAT = 1.1F;

    @Before
    public void initEnvironment(){
        invariantTree = new TraceInvariantList();
    }

    @Test
    public void addInvariantNode() {
        File testLog = new File(FileUtils.getCurrentDirectoryPath() + "TestLog_4unique_traces_each_duplicated_twice.xes");
        XLog log = null;
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

        invariantTree.addValue(ATTR_KEY_VAL_1, log, log.get(0).get(0));
        invariantTree.addValue(ATTR_KEY_VAL_2, log, log.get(0).get(1));

        Object val = invariantTree.getInvariantNodeForKey(ATTR_KEY_VAL_1).getAllAvailableValues().get(0);
        assertTrue(val.equals(ATTR_TEST_VAL_STRING_VAL_1));
        Object val2 = invariantTree.getInvariantNodeForKey(ATTR_KEY_VAL_2).getAllAvailableValues().get(1);
        assertTrue(val2.equals(ATTR_TEST_VAL_STRING_VAL_2));
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
    public void clearEnvironment(){
        invariantTree = null;
    }

}