package algorithms.search.invariant;
import algorithms.search.trace.locator.invariant.InvariantLogValidator;
import algorithms.search.trace.locator.invariant.Node;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import base.LogTestBaseClass;
import org.deckfour.xes.model.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InvariantLogValidatorTest extends LogTestBaseClass {

    @Test
    public void isValid() {
        XLog log = getLogInstance();
        TraceInvariantList list = new TraceInvariantList();

        XAttributeMap attributes = log.get(0).get(0).getAttributes();

        for (String key: attributes.keySet()) {
            list.addInvariantNode(new Node(key));
        }

        InvariantLogValidator invariantLogValidator = new InvariantLogValidator(list);
        assertTrue(invariantLogValidator.isValid(log));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isValidCheckException() {
        XLog log = getLogInstance();
        TraceInvariantList list = new TraceInvariantList();

        XAttributeMap attributes = log.get(0).get(0).getAttributes();

        int skipAttributesCount = 2;
        int skippedVals = -1;
        for (String key: attributes.keySet()) {
            if (skippedVals < skipAttributesCount){
                skippedVals ++;
                continue;
            }
            list.addInvariantNode(new Node(key));
        }

        InvariantLogValidator  invariantLogValidator = new InvariantLogValidator(list);
        invariantLogValidator.isValid(log);
    }
}