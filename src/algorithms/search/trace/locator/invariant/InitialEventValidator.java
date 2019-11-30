package algorithms.search.trace.locator.invariant;

import algorithms.search.trace.ITraceSearchingAlgorithm;
import algorithms.search.trace.locator.invariant.rule.log.Initial;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import java.util.List;

import static algorithms.search.trace.TraceSearchingAlgorithm.ADD_NEW_TRACE;
import static algorithms.search.trace.TraceSearchingAlgorithm.TRACE_UNDEFINED;

/**
 * Defines if an event is applicable to be an initial or not
 */
public class InitialEventValidator {

    private List<Initial> initialValues;

    public InitialEventValidator(TraceInvariantList initialValues) {
        this.initialValues = initialValues.getInitialEvents();
    }


    public int[] defineSuitableTracesList(XLog xLog, XEvent event) {
        for (Initial initialValue : initialValues) {
            if (!initialValue.isInitialState(event)) {
                return TRACE_UNDEFINED;
            }
        }
        return ADD_NEW_TRACE;
    }

}
