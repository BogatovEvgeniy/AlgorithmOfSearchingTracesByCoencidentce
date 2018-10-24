package algorithms.search.invariant;

import algorithms.search.base.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.*;

public class InvariantLogValidator implements ITraceSearchingAlgorithm.TraceLocator.ILogValidator {

    private TraceInvariantList invariantTree;

    public InvariantLogValidator(TraceInvariantList invariantTree) {
        this.invariantTree = invariantTree;
    }

    @Override
    public boolean isValid(XLog xLog) throws IllegalArgumentException {
        List<String> skippedKeys = new LinkedList();
        Set<String> attributesKeySet = new HashSet<>();
        for (XTrace trace : xLog) {
            for (XEvent xEvent : trace) {
                attributesKeySet = xEvent.getAttributes().keySet();

            }
        }

        for (String key : attributesKeySet) {
            Node node = invariantTree.getInvariantNodeForKey(key);
            if (node != null && node.getKey().equals(key)) {
                    continue;
            } else {
                skippedKeys.add(key);
            }
        }

        if (skippedKeys.size() > 0) {
            throw new IllegalArgumentException("The log doesn't contain all Attributes mentioned in InvariantTree. Skipped keys:"
                    + Arrays.toString(skippedKeys.toArray()));
        } else {
            return true;
        }
    }
}
