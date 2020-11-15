package algorithms.search.trace;

import algorithms.ValidationFactory;
import algorithms.search.trace.locator.coefficient.LastEventCoefficientsTraceLocator;
import algorithms.search.trace.locator.invariant.ByFirstTraceCoincidenceInvariantsTraceLocator;
import algorithms.search.trace.locator.invariant.InitialEventValidator;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import com.sun.istack.internal.NotNull;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import usecases.Product_production;

import java.util.*;

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
    public static final int TRACE_UNDEFINED_INDEX = -1;
    public static final int TRACE_ADD_NEW_TRACE_INDEX = -2;
    public static final int[] TRACE_UNDEFINED = new int[]{TRACE_UNDEFINED_INDEX};
    public static final int[] ADD_NEW_TRACE = new int[]{TRACE_ADD_NEW_TRACE_INDEX};
    private XLog originLog;
    private XLog resultLog;
    private ILocatorResultMerger locatorResultMerger;
    private InitialEventValidator initialEventValidator;
    private Map<String, TraceLocator> traceLocators;


    public TraceSearchingAlgorithm() {
    }

    public TraceSearchingAlgorithm(ILocatorResultMerger locatorResultMerger, TraceLocator... traceLocators) {
        this.locatorResultMerger = locatorResultMerger;

        for (TraceLocator traceLocator : traceLocators) {
            setTraceLocator(traceLocator);
        }
    }

    public TraceSearchingAlgorithm(ILocatorResultMerger locatorResultMerger, InitialEventValidator initialEventValidator, TraceLocator... traceLocators) {
        this.locatorResultMerger = locatorResultMerger;
        this.initialEventValidator = initialEventValidator;

        for (TraceLocator traceLocator : traceLocators) {
            setTraceLocator(traceLocator);
        }
    }


    public static ITraceSearchingAlgorithm initAlgorithmBasedOnAttributeComparision(Map<String, Float> correctionMap) {
        //--------------------------- WEIGHTS BASED SEARCH ALGORITHM ---------------------------------//
        ITraceSearchingAlgorithm.TraceLocator traceLocator = new LastEventCoefficientsTraceLocator(0.6f, correctionMap);
        return initTraceSearchingAlgorithm(traceLocator);
    }

    public static ITraceSearchingAlgorithm initAlgorithmBasedOnInvariantComparision(TraceInvariantList tree) {
        InitialEventValidator initialEventValidator = new InitialEventValidator(tree);
        ITraceSearchingAlgorithm.TraceLocator invariantTraceLocator = new ByFirstTraceCoincidenceInvariantsTraceLocator(0.9f, tree);
        return initTraceSearchingAlgorithm(initialEventValidator, invariantTraceLocator);
    }

    private static ITraceSearchingAlgorithm initTraceSearchingAlgorithm(ITraceSearchingAlgorithm.TraceLocator traceLocator) {
        // Launch the algorithm of searching traces by coincidences of event's attributes values
        // also tacking in a count coefficientMap
        TraceSearchingAlgorithm searchingAlgorithm = new TraceSearchingAlgorithm();

        // Define locators
        searchingAlgorithm.setTraceLocator(traceLocator);
        return searchingAlgorithm;
    }

    private static ITraceSearchingAlgorithm initTraceSearchingAlgorithm(InitialEventValidator initialEventValidator, ITraceSearchingAlgorithm.TraceLocator traceLocator) {
        // Launch the algorithm of searching traces by coincidences of event's attributes values
        // also tacking in a count coefficientMap
        TraceSearchingAlgorithm searchingAlgorithm = new TraceSearchingAlgorithm();

        // Define locators
        searchingAlgorithm.setTraceLocator(traceLocator);

        searchingAlgorithm.setInitialValuesValidator(initialEventValidator);
        return searchingAlgorithm;
    }

    private void setInitialValuesValidator(InitialEventValidator initialEventValidator) {
        this.initialEventValidator = initialEventValidator;
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

        XLog xTraces = removeSingleEventTraces(resultLog);

        // TODO Production log hardcoded values. Should be moved in Algorithm logic
//        XLog removeWrongInitialFinalValueTraces = removeWrongInitialFinalValueTraces(xTraces);
        return xTraces;
    }

    private XLog removeWrongInitialFinalValueTraces(XLog xTraces) {
        List<XTrace> tracesToExclude = new LinkedList<>();
        for (XTrace xTrace : xTraces) {
            XEvent initialEvent = xTrace.get(0);
            XEvent finalEvent = xTrace.get(xTrace.size() - 1);

            boolean initialEqual = initialEvent.getAttributes().get(Product_production.KEY_ACTIVITY).toString().equals("Turning & Milling");
            boolean finalIsEqual = finalEvent.getAttributes().get(Product_production.KEY_ACTIVITY).toString().equals("Packing");
            boolean comparisionResult = initialEqual && finalIsEqual;
            if(!comparisionResult){
                tracesToExclude.add(xTrace);
            }

        }
        boolean res = xTraces.removeAll(tracesToExclude);
        return xTraces;
    }

    private XLog removeSingleEventTraces(XLog resultLog) {
        Iterator<XTrace> iterator = resultLog.iterator();
        List<XTrace> traceToRemove = new LinkedList<>();
        while (iterator.hasNext()) {
            XTrace next = iterator.next();
            if (next.size() <= 1) {
                traceToRemove.add(next);
            }
        }

        for (XTrace xTrace : traceToRemove) {
            resultLog.remove(xTrace);
        }
        return resultLog;
    }

    @Override
    public String getResultsName() {
        StringBuilder result = new StringBuilder(this.getClass().getSimpleName());
        for (String key : traceLocators.keySet()) {
            result.append("_");
            result.append(traceLocators.get(key).getClass().getSimpleName());
        }
        return result.toString();
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


    private void insertEventInLogByLocators(XLog resultLog, XEvent xEvent) {
        // Insert first event if result log is empty
        if (proceedEventForEmptyResultLog(resultLog, xEvent)) return;
        int[] traceLocatorResults = calculateLocatorResults(resultLog, xEvent);

        addEvent(xEvent, traceLocatorResults);

    }

    private void addEvent(XEvent xEvent, int[] traceLocatorResults) {

        if (initialEventValidator == null) {
            if (Arrays.equals(traceLocatorResults, TRACE_UNDEFINED)) {
                addInNewTrace(xEvent);
            } else {
                addByTheIndex(xEvent, traceLocatorResults[0]);
            }
        } else {
            /**
             * Can event be an initial or not
             */
            if (Arrays.equals(traceLocatorResults, TRACE_UNDEFINED)) {
                traceLocatorResults = initialEventValidator.defineSuitableTracesList(resultLog, xEvent);
            }

            if (Arrays.equals(traceLocatorResults, TRACE_UNDEFINED)) {
                return;
            } else if (Arrays.equals(traceLocatorResults, ADD_NEW_TRACE)) {
                addInNewTrace(xEvent);
            } else {
                addByTheIndex(xEvent, traceLocatorResults[0]);
            }
        }
    }

    private void addByTheIndex(XEvent xEvent, int traceLocatorResult1) {
        int traceLocatorResult = traceLocatorResult1;
        resultLog.get(traceLocatorResult).add(xEvent);
    }

    private void addInNewTrace(XEvent xEvent) {
        XTraceImpl trace = new XTraceImpl(new XAttributeMapLazyImpl<>(XAttributeMapImpl.class));
        resultLog.add(trace);
        trace.add(xEvent);
    }

    private int[] calculateLocatorResults(XLog resultLog, XEvent xEvent) {
        Iterator<String> iterator = traceLocators.keySet().iterator();
        int[] mergedResults = new int[]{};
        while (iterator.hasNext()) {
            String next = iterator.next();
            TraceLocator traceLocator = traceLocators.get(next);
            if (traceLocator.getLogValidator().isValid(originLog)) {
                int[] suitableTraces = traceLocator.defineSuitableTracesList(resultLog, xEvent);
                if (traceLocators.size() > 1) {
                    mergedResults = locatorResultMerger.merge(suitableTraces);
                } else {
                    mergedResults = suitableTraces;
                }
            }
        }
        return mergedResults;
    }

    private boolean proceedEventForEmptyResultLog(XLog xLog, XEvent xEvent) {
        if (xLog.size() == 0) {
            if (initialEventValidator == null ||
                    Arrays.equals(initialEventValidator.defineSuitableTracesList(xLog, xEvent), ADD_NEW_TRACE)) {
                xLog.add(new XTraceImpl(new XAttributeMapLazyImpl<>(XAttributeMapImpl.class)));
                xLog.get(0).add(xEvent);
                return true;
            }
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
