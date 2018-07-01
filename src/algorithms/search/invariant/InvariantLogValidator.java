package algorithms.search.invariant;

import algorithms.search.base.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.*;

import java.util.Set;

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
                    Set<AttributeInvariantTree.TreeNode<XAttribute, String>> invariantsForKey = invariantTree.getInvariantsForKey(attributes.get(key));
                    for (AttributeInvariantTree.TreeNode<XAttribute, String> anInvariantsForKey : invariantsForKey) {
                        if (anInvariantsForKey.getValues().get(0).equals(attributes.get(key).toString())) {
                            return true;
                        }
                    }
                }
            }
        }
        throw new IllegalArgumentException("The log wasn't preprocessed by InvariantInitialEventSearchAlgorithm");
    }
}
