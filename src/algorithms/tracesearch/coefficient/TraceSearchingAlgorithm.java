package algorithms.tracesearch.coefficient;

import algorithms.ValidationFactory;
import algorithms.tracesearch.ITraceSearchingAlgorithm;
import algorithms.tracesearch.ILocatorResultMerger;
import algorithms.tracesearch.TraceValidator;
import algorithms.tracesearch.locators.CoincidenceTraceLocator;
import exceptions.LogParsingError;
import javafx.util.Pair;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

import java.io.File;
import java.util.*;

/**
 *  _____________________________________
 * |THE DESCRIPTION SHOULD BE UPDATED    |
 * |_____________________________________|
 *
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
    private final File srcFile;
    private XLog resultLog;
    private final TraceValidator traceValidator = new TraceValidator();
    private ILocatorResultMerger locatorResultMerger;
    private Map<String, TraceLocator> traceLocators;


    public TraceSearchingAlgorithm(File srcFile) {
        this.srcFile = srcFile;
        registerTraceLocator(new CoincidenceTraceLocator(0.8f, null));
    }

    public TraceSearchingAlgorithm(File srcFile, ILocatorResultMerger locatorResultMerger, TraceLocator ... traceLocators ) {
        this(srcFile);
        this.locatorResultMerger = locatorResultMerger;

        for (TraceLocator traceLocator : traceLocators) {
            registerTraceLocator(traceLocator);
        }
    }

    @Override
    public XLog proceed() throws LogParsingError {
        try {
            // Delegate this work to reader class
            XesXmlParser xUniversalParser = new XesXmlParser();
            if (xUniversalParser.canParse(srcFile)) {
                List<XLog> parsedLog = xUniversalParser.parse(srcFile);

                // Move out of code's block
                if (traceValidator.validateIsEmpty(parsedLog)) return  new XLogImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class));
                resultLog = buildTracesBasedOnInvariants(parsedLog);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if (resultLog == null) {
            throw new LogParsingError("Result log is empty. Impossible to build output file");
        }
        return resultLog;
    }

    @Override
    public void registerTraceLocator(TraceLocator traceLocator) {
        if (traceLocators == null) traceLocators = new HashMap<>();
        traceLocators.put(traceLocator.getId(), traceLocator);
    }

    @Override
    public void unregisterTraceLocator(String traceLocatorID) {
        traceLocators.remove(traceLocatorID);
    }

    private XLog buildTracesBasedOnInvariants(List<XLog> parsedLog) {
        resultLog = new XLogImpl(parsedLog.get(0).getAttributes());
        for (XEvent event : parsedLog.get(0).get(0)) {
            insertEventInLogByCriteria(resultLog, event, false);
        }
        return resultLog;
    }

    private void insertEventInLogByCriteria(XLog xLog, XEvent xEvent, boolean deepSearchByAllEvents) {
        // Insert first event if result log is empty
        if (proceedEventForEmptyResultLog(xLog, xEvent)) return;

        int [] traceLocatorResults = new int[]{};
        Iterator<String> iterator = traceLocators.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            int[] suitableTraces = traceLocators.get(next).defineTrace(xLog, xEvent);
            traceLocatorResults = locatorResultMerger.merge(suitableTraces);
        }

        // Insert value in a trace with highest coefficient
        if (traceLocatorResults == null && traceLocatorResults.length == 0) {
            XTraceImpl trace = new XTraceImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class));
            resultLog.add(trace);
            trace.add(xEvent);
        } else {
            resultLog.get(traceLocatorResults[0]).add(xEvent);
        }
    }

    private boolean proceedEventForEmptyResultLog(XLog xLog, XEvent xEvent) {
        if (xLog.size() == 0) {
            xLog.add(new XTraceImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class)));
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
