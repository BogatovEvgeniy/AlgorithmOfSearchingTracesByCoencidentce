package algorithms.preprocess;

import algorithms.ILogAlgorithm;
import algorithms.search.invariant.AttributeInvariantTree;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

import java.util.Iterator;
import java.util.List;

/**
 * Search trace where event has attribute with value mentioned in invariant list
 */
public class InvariantInitialEventSearchAlgorithm implements ILogAlgorithm {
    private AttributeInvariantTree invariantTree;

    public InvariantInitialEventSearchAlgorithm(AttributeInvariantTree invariantTree) {
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

                    AttributeInvariantTree.Node<String> invariantsForKey = invariantTree.getInvariantNodeForKey(attributes.get(key));
                    Iterator<String> iterator = invariantsForKey.getInvariantValues().iterator();
                    while (iterator.hasNext()) {
                        if (isInvariantValueIsEqualsToEventVal(result, trace, xEvent, attributes, key, iterator.next())) break;
                    }
                }
            }
        }
        return result;
    }

    private boolean isInvariantValueIsEqualsToEventVal(XLog result,
                                                       XTrace trace,
                                                       XEvent xEvent,
                                                       XAttributeMap attributes,
                                                       String key,
                                                       String value) {
        if (value.equals(attributes.get(key).toString())) {
            XTrace validTrace = new XTraceImpl(trace.getAttributes());
            validTrace.add(xEvent);
            result.add(trace);
            return true;
        }
        return false;
    }
}