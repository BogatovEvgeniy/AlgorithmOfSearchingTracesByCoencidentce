package algorithms.search.trace.locator.invariant;

import algorithms.search.trace.ITraceSearchingAlgorithm;
import algorithms.search.trace.locator.invariant.rule.log.Initial;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import java.util.Arrays;
import java.util.List;

import static algorithms.search.trace.TraceSearchingAlgorithm.ADD_NEW_TRACE;
import static algorithms.search.trace.TraceSearchingAlgorithm.TRACE_UNDEFINED;

/**
 * Defines if an event is applicable to be an initial or not
 */
public class InitialEventValidator {

    private TraceInvariantList invariantList;

    public InitialEventValidator(TraceInvariantList invariantList) {
        this.invariantList = invariantList;
    }


    public int[] defineSuitableTracesList(XLog xLog, XEvent event) {
        int[] result = TRACE_UNDEFINED;
        for (String attrKey : event.getAttributes().keySet()) {
            List<Initial> initialEventsPerKey = invariantList.getInitialEvents(attrKey);

            if (initialEventsPerKey == null) {
                continue;
            }

            int[] perKeyResult = TRACE_UNDEFINED;
            for (Initial initialValue : initialEventsPerKey) {
                if (initialValue.isInitialState(event)) {
                    perKeyResult = ADD_NEW_TRACE;
                }
            }
            result = perKeyResult;
            if (Arrays.equals(TRACE_UNDEFINED, result)) {
                break;
            }
        }

        return result;
    }

}
