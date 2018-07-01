package algorithms.search;

import algorithms.ValidationFactory;
import algorithms.search.base.ILocatorResultMerger;
import algorithms.search.base.ITraceSearchingAlgorithm;
import com.sun.istack.internal.NotNull;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * _____________________________________
 * |THE DESCRIPTION SHOULD BE UPDATED    |
 * |_____________________________________|
 * <p>
 * The algorithm of parsing logs consists only of events
 * Algorithm allows to group events by traces basing on maximal coinsidence of comparing events.
 * While algorithm proceed sets of events we were assumed that
 * - the order of events reflects order of their execution in business - process of production
 *
 * @Deprecated The algorithm consists of the next steps
 * - get the last event of the trace
 * - start comparision of events one by one basing on maximal confident criteria
 * - while the last event of the log was met or no coefficient was found
 * - if no coefficient was found the event or group event will be copied in undefinedEvents list, till first cycle of events comparision was finished
 * - if last event was met that next event will be copied in a new trace
 * <p>
 * skipped now - when first cycle of events comparison was finished, that second cycle will be started, where each event from undefined events list is comparing with traces built on the previous cycle
 * <p>
 * criteria will be used for coincident and non coincident events
 */

public class TraceSearchingAlgorithm implements ITraceSearchingAlgorithm {
    private XLog originLog;
    private XLog resultLog;
    private ILocatorResultMerger locatorResultMerger;
    private Map<String, TraceLocator> traceLocators;


    public TraceSearchingAlgorithm() {
    }

    public TraceSearchingAlgorithm(ILocatorResultMerger locatorResultMerger, TraceLocator... traceLocators) {
        this.locatorResultMerger = locatorResultMerger;

        for (TraceLocator traceLocator : traceLocators) {
            setTraceLocator(traceLocator);
        }
    }


    @Override
    public XLog proceed(@NotNull XLog originLog) {
        if (traceLocators == null || traceLocators.size() == 0) {
            throw new IllegalStateException("Provide at least one trace locator instance for searching traces");
        }

        this.originLog = originLog;
        resultLog = new XLogImpl(originLog.getAttributes());
        for (XEvent event : originLog.get(0)) {
            insertEventInLogByLocators(resultLog, event);
        }
        return resultLog;
    }

    @Override
    public void setTraceLocator(TraceLocator traceLocator) {
        if (traceLocators == null) traceLocators = new HashMap<>();
        traceLocators.clear();
        traceLocators.put(traceLocator.getId(), traceLocator);
    }


    @Override
    public void setTraceLocators(ILocatorResultMerger locatorResultMerger, TraceLocator... traceLocator) {
        if (traceLocators == null) traceLocators = new HashMap<>();
        traceLocators.clear();
        this.locatorResultMerger = locatorResultMerger;
        for (TraceLocator locator : traceLocator) {
            traceLocators.put(locator.getId(), locator);
        }
    }


    private void insertEventInLogByLocators(XLog xLog, XEvent xEvent) {
        // Insert first event if result log is empty
        if (proceedEventForEmptyResultLog(xLog, xEvent)) return;

        int[] traceLocatorResults = new int[]{};
        traceLocatorResults = getLocatorsMergedResults(xLog, xEvent, traceLocatorResults);

        if (traceLocatorResults == null || traceLocatorResults.length == 0) {
            XTraceImpl trace = new XTraceImpl(new XAttributeMapLazyImpl<>(XAttributeMapImpl.class));
            resultLog.add(trace);
            trace.add(xEvent);
        } else {
            resultLog.get(traceLocatorResults[0]).add(xEvent);
        }
    }

    private int[] getLocatorsMergedResults(XLog xLog, XEvent xEvent, int[] traceLocatorResults) {
        Iterator<String> iterator = traceLocators.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            TraceLocator traceLocator = traceLocators.get(next);
            if (traceLocator.getLogValidator().isValid(originLog)) {
                int[] suitableTraces = traceLocator.defineSuitableTracesList(xLog, xEvent);
                if (traceLocators.size() > 1) {
                    traceLocatorResults = locatorResultMerger.merge(suitableTraces);
                } else {
                    traceLocatorResults = suitableTraces;
                }
            }
        }
        return traceLocatorResults;
    }

    private boolean proceedEventForEmptyResultLog(XLog xLog, XEvent xEvent) {
        if (xLog.size() == 0) {
            xLog.add(new XTraceImpl(new XAttributeMapLazyImpl<>(XAttributeMapImpl.class)));
            xLog.get(0).add(xEvent);
            return true;
        }
        return false;
    }


    private Map<Integer, Integer> getCoincidencesByInvariants(XLog xLog, XEvent xEvent) {
        Map<Integer, Integer> resultMap = new HashMap<>();
        for (int i = 0; i < xLog.size(); i++) {
            for (XEvent eventInTrace : xLog.get(i)) {
                int coincidenceValue = ValidationFactory.maxAttributesCoincidences(eventInTrace, xEvent);
                if (!resultMap.containsKey(i) || (resultMap.containsKey(i) && resultMap.get(i) < coincidenceValue)) {
                    resultMap.put(i, coincidenceValue);
                }
            }
        }
        return resultMap;
    }
}
