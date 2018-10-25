package algorithms.search.invariant;

import base.LogTestBaseClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class InvariantsTraceLocatorTest extends LogTestBaseClass {

    private static final String ATTR_1 = "attr1";
    private static final String ATTR_2 = "attr2";
    private static final String ATTR_3 = "attr3";
    private static final String ATTR_4 = "attr4";

    @Test
    public void getId() {
        assertTrue(new InvariantsTraceLocator(new TraceInvariantList()).getId().equals(InvariantsTraceLocator.class.getSimpleName()));
    }


    /**
     * Test log is "TestLog_4unique_traces_for_invariant_test.xes"
     *
     *  Invariant
     * key="attr1" value="val1, val2, val3"
     * key="attr2" value="val1, val2, val3"
     * key="attr3" value="val1, val2, val3"
     * key="attr4" value="val1, val2, val3"
     *
     * Invariants for test
     */
    @Test
    public void defineSuitableTracesList() {
        XLog logInstance = getLogInstance();

        //Check data
        List<String> invariantForAllKeys = Arrays.asList("val1", "val2", "val3");
        List <String> trace_attr1 = Arrays.asList("val1", "val2", "val3");
        List <String> trace_attr2 = Arrays.asList("val1", "val3");
        List <String> trace_attr3 = Arrays.asList("val1", "val2");
        List <String> trace_attr4 = Arrays.asList("val1");

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

        InvariantsTraceLocator invariantsTraceLocator = new InvariantsTraceLocator(tree);
        for (XTrace trace : logInstance) {
            for (XEvent event : trace) {
                invariantsTraceLocator.defineSuitableTracesList(logInstance, event);
            }
        }

        assertTrue(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().get(0).containsAll(trace_attr1));
        assertTrue(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().get(1).containsAll(trace_attr1));
        assertTrue(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().get(2).containsAll(trace_attr1));
        assertTrue(tree.getInvariantNodeForKey(ATTR_1).getAllAvailableValues().get(3).containsAll(trace_attr1));

        assertTrue(tree.getInvariantNodeForKey(ATTR_2).getAllAvailableValues().get(0).containsAll(trace_attr2));
        assertTrue(tree.getInvariantNodeForKey(ATTR_2).getAllAvailableValues().get(1).containsAll(trace_attr2));
        assertTrue(tree.getInvariantNodeForKey(ATTR_2).getAllAvailableValues().get(2).containsAll(trace_attr2));
        assertTrue(tree.getInvariantNodeForKey(ATTR_2).getAllAvailableValues().get(3).containsAll(trace_attr2));

        assertTrue(tree.getInvariantNodeForKey(ATTR_3).getAllAvailableValues().get(0).containsAll(trace_attr3));
        assertTrue(tree.getInvariantNodeForKey(ATTR_3).getAllAvailableValues().get(1).containsAll(trace_attr3));
        assertTrue(tree.getInvariantNodeForKey(ATTR_3).getAllAvailableValues().get(2).containsAll(trace_attr3));
        assertTrue(tree.getInvariantNodeForKey(ATTR_3).getAllAvailableValues().get(3).containsAll(trace_attr3));

        assertTrue(tree.getInvariantNodeForKey(ATTR_4).getAllAvailableValues().get(0).containsAll(trace_attr4));
        assertTrue(tree.getInvariantNodeForKey(ATTR_4).getAllAvailableValues().get(1).containsAll(trace_attr4));
        assertTrue(tree.getInvariantNodeForKey(ATTR_4).getAllAvailableValues().get(2).containsAll(trace_attr4));
        assertTrue(tree.getInvariantNodeForKey(ATTR_4).getAllAvailableValues().get(3).containsAll(trace_attr4));
    }

    @Test
    public void getLogValidator() {
    }
}