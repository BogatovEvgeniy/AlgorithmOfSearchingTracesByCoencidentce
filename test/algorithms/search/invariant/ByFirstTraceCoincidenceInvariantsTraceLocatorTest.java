package algorithms.search.invariant;

import algorithms.search.TraceSearchingAlgorithm;
import base.LogTestBaseClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ByFirstTraceCoincidenceInvariantsTraceLocatorTest extends LogTestBaseClass {

    private static final String ATTR_1 = "attr1";
    private static final String ATTR_2 = "attr2";
    private static final String ATTR_3 = "attr3";
    private static final String ATTR_4 = "attr4";

    @Test
    public void getId() {
        assertTrue(new ByFirstTraceCoincidenceInvariantsTraceLocator(0.7f, new TraceInvariantList()).getId().equals(ByFirstTraceCoincidenceInvariantsTraceLocator.class.getSimpleName()));
    }


    /**
     * Test log is "TestLog_4unique_traces_for_invariant_test.xes"
     * <p>
     * Invariant
     * key="attr1" value="val1, val2, val3"
     * key="attr2" value="val1, val2, val3"
     * key="attr3" value="val1, val2, val3"
     * key="attr4" value="val1, val2, val3"
     * <p>
     * Invariants for test
     */
    @Test
    public void defineSuitableTracesList() {
        XLog logInstance = getLogInstance();

        //Check data
        List<String> invariantForAllKeys = Arrays.asList("val1", "val2", "val3");
        List<String> trace_attr1 = Arrays.asList("val1", "val2", "val3");
        List<String> trace_attr2 = Arrays.asList("val2", "val4");
        List<String> trace_attr3 = Arrays.asList("val1", "val2", "val3");
        List<String> trace_attr4 = Arrays.asList("val3");

        TraceInvariantList tree = new TraceInvariantList();

        Node nodeATTR_1 = new Node(ATTR_1);
        Node nodeATTR_2 = new Node(ATTR_2);
        Node nodeATTR_3 = new Node(ATTR_3);
        Node nodeATTR_4 = new Node(ATTR_4);

        nodeATTR_1.addInvariant(invariantForAllKeys);
        nodeATTR_2.addInvariant(invariantForAllKeys);
        nodeATTR_3.addInvariant(invariantForAllKeys);
        nodeATTR_4.addInvariant(invariantForAllKeys);

        tree.addInvariantNode(nodeATTR_1);
        tree.addInvariantNode(nodeATTR_2);
        tree.addInvariantNode(nodeATTR_3);
        tree.addInvariantNode(nodeATTR_4);

        TraceSearchingAlgorithm searchingAlgorithm = new TraceSearchingAlgorithm();
        ByFirstTraceCoincidenceInvariantsTraceLocator byFirstTraceCoincidenceInvariantsTraceLocator = new ByFirstTraceCoincidenceInvariantsTraceLocator(0, tree);
        searchingAlgorithm.setTraceLocator(byFirstTraceCoincidenceInvariantsTraceLocator);
        XLogImpl resultLog = new XLogImpl(logInstance.getAttributes());
        for (XTrace trace : logInstance) {
            for (XEvent event : trace) {
                int[] indexes = byFirstTraceCoincidenceInvariantsTraceLocator.defineSuitableTracesList(resultLog, event);
                List<Integer> traceIndexes = new LinkedList<>();

                if (indexes == null) {
                    resultLog.add(new XTraceImpl(new XAttributeMapLazyImpl<>(XAttributeMapImpl.class)));
                    resultLog.get(resultLog.size() - 1).add(event);
                    continue;
                }

                for (int index : indexes) {
                    traceIndexes.add(index);
                }

                Collections.sort(traceIndexes);
                Integer biggestValueIndex = traceIndexes.get(traceIndexes.size() - 1);
                resultLog.get(biggestValueIndex).add(event);
            }
        }

        assertNotNull(tree);
        assertNotNull(tree.getInvariantNodeForKey(ATTR_1));
        assertNotNull(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues());
        assertTrue(!tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().isEmpty());

        assertTrue(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().get(0).containsAll(trace_attr1));
        assertTrue(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().get(1).containsAll(trace_attr2));
        assertTrue(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().get(2).containsAll(trace_attr3));
        assertTrue(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().get(3).containsAll(trace_attr4));

        assertTrue(tree.getInvariantNodeForKey(ATTR_2).getAllAvailableValues().get(0).containsAll(trace_attr1));
        assertTrue(tree.getInvariantNodeForKey(ATTR_2).getAllAvailableValues().get(1).containsAll(trace_attr2));
        assertTrue(tree.getInvariantNodeForKey(ATTR_2).getAllAvailableValues().get(2).containsAll(trace_attr3));
        assertTrue(tree.getInvariantNodeForKey(ATTR_2).getAllAvailableValues().get(3).containsAll(trace_attr4));

        assertTrue(tree.getInvariantNodeForKey(ATTR_3).getAllAvailableValues().get(0).containsAll(trace_attr1));
        assertTrue(tree.getInvariantNodeForKey(ATTR_3).getAllAvailableValues().get(1).containsAll(trace_attr2));
        assertTrue(tree.getInvariantNodeForKey(ATTR_3).getAllAvailableValues().get(2).containsAll(trace_attr3));
        assertTrue(tree.getInvariantNodeForKey(ATTR_3).getAllAvailableValues().get(3).containsAll(trace_attr4));

        assertTrue(tree.getInvariantNodeForKey(ATTR_4).getAllAvailableValues().get(0).containsAll(trace_attr1));
        assertTrue(tree.getInvariantNodeForKey(ATTR_4).getAllAvailableValues().get(1).containsAll(trace_attr2));
        assertTrue(tree.getInvariantNodeForKey(ATTR_4).getAllAvailableValues().get(2).containsAll(trace_attr3));
        assertTrue(tree.getInvariantNodeForKey(ATTR_4).getAllAvailableValues().get(3).containsAll(trace_attr4));
    }

    @Test
    public void getLogValidator() {
    }
}