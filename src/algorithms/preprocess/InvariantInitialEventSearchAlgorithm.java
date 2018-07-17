package algorithms.preprocess;

import algorithms.ILogAlgorithm;
import algorithms.search.invariant.AttributeInvariantTree;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

import java.util.Iterator;
import java.util.Set;

/**
 * Search trace where event has attribute with value mentioned in invariant list
 */
public class InvariantInitialEventSearchAlgorithm implements ILogAlgorithm {
    private AttributeInvariantTree<String> invariantTree;

    public InvariantInitialEventSearchAlgorithm(AttributeInvariantTree<String> invariantTree) {
        this.invariantTree = invariantTree;
    }

    @Override
    public XLog proceed(XLog origin) {
        XLog result = new XLogImpl(origin.getAttributes());
        for (XTrace trace : origin) {

            if (!result.isEmpty()) {
                result.add(trace);
            }

            for (XEvent xEvent : trace) {

                if (!result.isEmpty()) {
                    result.get(result.size()-1).add(xEvent);
                }

                XAttributeMap attributes = xEvent.getAttributes();
                for (String key : attributes.keySet()) {
                    if (!result.isEmpty()) {
                        break;
                    }

                    Set<AttributeInvariantTree.TreeNode<XAttribute, String>> invariantsForKey = invariantTree.getInvariantsForKey(attributes.get(key));
                    Iterator<AttributeInvariantTree.TreeNode<XAttribute, String>> iterator = invariantsForKey.iterator();
                    while (iterator.hasNext()) {
                        if (isInvariantValueIsEqualsToEventVal(result, trace, xEvent, attributes, key, iterator)) break;
                    }
                }
            }
        }
        return result;
    }

    private boolean isInvariantValueIsEqualsToEventVal(XLog result, XTrace trace, XEvent xEvent, XAttributeMap attributes, String key, Iterator<AttributeInvariantTree.TreeNode<XAttribute, String>> iterator) {
        if (iterator.next().getValues().get(0).equals(attributes.get(key).toString())) {
            XTrace validTrace = new XTraceImpl(trace.getAttributes());
            validTrace.add(xEvent);
            result.add(trace);
            return true;
        }
        return false;
    }
}