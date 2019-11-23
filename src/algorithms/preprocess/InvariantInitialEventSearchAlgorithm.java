package algorithms.preprocess;

import algorithms.ILogAlgorithm;
import algorithms.search.trace.locator.invariant.Node;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

import java.util.Iterator;


//TODO Requires recheck of logic
/**
 * Search trace where event has attribute with value mentioned in invariant list
 */
@Deprecated
public class InvariantInitialEventSearchAlgorithm implements ILogAlgorithm<XLog> {
    private TraceInvariantList invariantTree;

    public InvariantInitialEventSearchAlgorithm(TraceInvariantList invariantTree) {
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

                    Node invariantsForKey = invariantTree.getRuleSetPerKey(attributes.get(key).getKey());
                    Iterator<String> iterator = invariantsForKey.getAttributeInvariant().iterator();
                    while (iterator.hasNext()) {
                        if (isInvariantValueIsEqualsToEventVal(result, trace, xEvent, attributes, key, iterator.next())) break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getResultsName() {
        return this.getClass().getSimpleName();
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