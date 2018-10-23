package algorithms.search.invariant;

import algorithms.search.base.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.*;

public class InvariantLogValidator implements ITraceSearchingAlgorithm.TraceLocator.ILogValidator {

    private TraceInvariantList invariantTree;

    public InvariantLogValidator(TraceInvariantList invariantTree) {
        this.invariantTree = invariantTree;
    }

    @Override
    public boolean isValid(XLog xLog) throws IllegalArgumentException {
        for (XTrace trace : xLog) {
            for (XEvent xEvent : trace) {
                XAttributeMap attributes = xEvent.getAttributes();
                for (String key : attributes.keySet()) {
                    Node node = invariantTree.getInvariantNodeForKey(attributes.get(key).getKey());
                    for (Object val : node.getAttributeInvariant()) {
                        if (val.equals(attributes.get(key).toString())) {
                            return true;
                        }
                    }
                }
            }
        }
        throw new IllegalArgumentException("The log wasn't preprocessed by InvariantInitialEventSearchAlgorithm");
    }
}
