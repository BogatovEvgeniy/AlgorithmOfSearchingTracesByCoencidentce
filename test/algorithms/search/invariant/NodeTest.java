package algorithms.search.invariant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class NodeTest {

    private static String ATTR_KEY_1 = "attr1";
    private static final String TEST_VAL = "val1";
    private Node node;

    @Before
    public void setUp() throws Exception {
        node = new Node(ATTR_KEY_1);
    }

    @Test
    public void addInvariant() {
        String[] invariantListTestData = {"A", "B", "C", "D"};
        List<String> invariantList = new LinkedList<>();
        invariantList.addAll(Arrays.asList(invariantListTestData));
        node.addInvariant(invariantList);

        assertNotNull(invariantList);
        assertNotNull(node);
        assertTrue(node.getAttributeInvariant().size() == invariantListTestData.length);
        assertTrue(node.getAttributeInvariant().containsAll(invariantList));
    }

    @Test
    public void addTrace() {
        String[] trace1 = {"A", "B", "C"};
        String[] trace2 = {"A", "C", "D"};
        node.addTrace(Arrays.asList(trace1));
        node.addTrace(Arrays.asList(trace2));

        assertNotNull(node);
        assertTrue(node.getAllAvailableValues().size() == 2);
    }

    @Test
    public void removeTraceByIndex() {
        String[] trace1 = {"A", "B", "C"};
        String[] trace2 = {"A", "C", "D"};
        List<String> traceValues1 = Arrays.asList(trace1);
        node.addTrace(traceValues1);
        List<String> traceValues2 = Arrays.asList(trace2);
        node.addTrace(traceValues2);
        node.removeTrace(traceValues1);

        assertNotNull(node);
        assertTrue(node.getAllAvailableValues().size() == 1);
    }


    @Test
    public void removeTraceByInstance() {
        String[] trace1 = {"A", "B", "C"};
        String[] trace2 = {"A", "C", "D"};
        List<String> traceValues1 = Arrays.asList(trace1);
        node.addTrace(traceValues1);
        List<String> traceValues2 = Arrays.asList(trace2);
        node.addTrace(traceValues2);
        List<List<String>> allAvailableValues = node.getAllAvailableValues();
        int traceIndex = -1;
        if (allAvailableValues.contains(traceValues1)) {
            Iterator<List<String>> iterator = allAvailableValues.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                if (iterator.next().equals(traceValues1)) {
                    traceIndex = i;
                    break;
                }

            }
        }

        assertTrue(traceIndex != -1);
        node.removeTrace(traceIndex);

        assertNotNull(node);
        assertTrue(node.getAllAvailableValues().size() == 1);
    }

    @Test
    public void addValue() {
        int traceIndex = 0;
        node.addTrace(new LinkedList<>());
        node.addValue(traceIndex, TEST_VAL);

        assertNotNull(node);
        assertTrue(node.getAllAvailableValues().size() == 1);
        assertTrue(node.getAllAvailableValues().get(traceIndex).get(0).equals(TEST_VAL));

    }

    @Test
    public void getKey() {
        Node newNode = new Node(ATTR_KEY_1);
        assertTrue(newNode.getKey().equals(ATTR_KEY_1));
    }

    @Test
    public void getAttributeInvariant() {
        String[] invariantListTestData = {"A", "B", "C", "D"};
        List<String> invariantList = new LinkedList<>();
        invariantList.addAll(Arrays.asList(invariantListTestData));
        node.addInvariant(invariantList);
        node.getAttributeInvariant();

        assertNotNull(invariantList);
        assertNotNull(node);
        assertTrue(node.getAttributeInvariant().size() == invariantListTestData.length);
        assertTrue(node.getAttributeInvariant().containsAll(invariantList));
    }

    @Test
    public void getAllAvailableValues() {
        String[] trace1 = {"A", "B", "C"};
        String[] trace2 = {"A", "C", "D"};
        List<String> traceValues1 = Arrays.asList(trace1);
        node.addTrace(traceValues1);
        List<String> traceValues2 = Arrays.asList(trace2);
        node.addTrace(traceValues2);
        List<List<String>> allAvailableValues = node.getAllAvailableValues();

        int commonSize = calculateCommonSize(allAvailableValues);

        assertNotNull(allAvailableValues);
        assertTrue(allAvailableValues.contains(traceValues1));
        assertTrue(allAvailableValues.contains(traceValues2));
        assertTrue(commonSize == (traceValues1.size() + traceValues2.size()));

    }

    private int calculateCommonSize(List<List<String>> allAvailableValues) {
        int commonSize = 0;
            Iterator<List<String>> iterator = allAvailableValues.iterator();
            while (iterator.hasNext()){
                commonSize += iterator.next().size();
        }
        return commonSize;
    }

    @Test
    public void clear() {
        String[] trace1 = {"A", "B", "C"};
        String[] trace2 = {"A", "C", "D"};
        List<String> traceValues1 = Arrays.asList(trace1);
        node.addTrace(traceValues1);
        List<String> traceValues2 = Arrays.asList(trace2);
        node.addTrace(traceValues2);
        List<List<String>> allAvailableValues = node.getAllAvailableValues();

        assertNotNull(allAvailableValues);
        assertTrue(calculateCommonSize(allAvailableValues) == (traceValues1.size() + traceValues2.size()));

        node.clear();
        allAvailableValues = node.getAllAvailableValues();

        assertNotNull(allAvailableValues);
        assertTrue(calculateCommonSize(allAvailableValues) == 0);
    }



    @After
    public void tearDown() throws Exception {
        node = null;
    }
}