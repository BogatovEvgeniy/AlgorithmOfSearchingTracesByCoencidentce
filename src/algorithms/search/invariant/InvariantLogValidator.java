package algorithms.search.invariant;

import algorithms.search.base.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.*;

import java.util.List;

public class InvariantLogValidator implements ITraceSearchingAlgorithm.TraceLocator.ILogValidator {

    private AttributeInvariantTree<String> invariantTree;

    public InvariantLogValidator(AttributeInvariantTree<String> invariantTree) {
        this.invariantTree = invariantTree;
    }

    @Override
    public boolean isValid(XLog xLog) throws IllegalArgumentException {
        for (XTrace trace : xLog) {
            for (XEvent xEvent : trace) {
                XAttributeMap attributes = xEvent.getAttributes();
                for (String key : attributes.keySet()) {
                    List<AttributeInvariantTree.Node<XAttribute, String>> invariantsForKey = invariantTree.getInvariantNodeForKey(attributes.get(key));
                    for (AttributeInvariantTree.Node<XAttribute, String> anInvariantsForKey : invariantsForKey) {
                        if (anInvariantsForKey.getInvariantValues().get(0).equals(attributes.get(key).toString())) {
                            return true;
                        }
                    }
                }
            }
        }
        throw new IllegalArgumentException("The log wasn't preprocessed by InvariantInitialEventSearchAlgorithm");
    }
}
