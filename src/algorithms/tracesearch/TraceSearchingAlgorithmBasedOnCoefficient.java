package algorithms.tracesearch;

import algorithms.ITraceSearchingAlgorithm;
import algorithms.ValidationFactory;
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
 * - while the last event of the log was met or no coincidence was found
 * - if no coincidence was found the event or group event will be copied in undefinedEvents list, till first cycle of events comparision was finished
 * - if last event was met that next event will be copied in a new trace
 * <p>
 * skipped now - when first cycle of events comparison was finished, that second cycle will be started, where each event from undefined events list is comparing with traces built on the previous cycle
 * <p>
 * criteria will be used for coincident and non coincident events
 */

public class TraceSearchingAlgorithmBasedOnCoefficient implements ITraceSearchingAlgorithm {
    private final File srcFile;
    private final TraceValidator traceValidator = new TraceValidator();
    private Map<String, Float> attributeCoefficientMap;
    private float minimalCoincidenceValue;

    private List<XEvent> undefinedEvents = new ArrayList<>();
    private XLog resultLog;

    public TraceSearchingAlgorithmBasedOnCoefficient(File srcFile, float minimalCoincidenceValue) {
        this.srcFile = srcFile;
        this.minimalCoincidenceValue = minimalCoincidenceValue;
    }

    public TraceSearchingAlgorithmBasedOnCoefficient(File srcFile, Map<String, Float> attributeCoefficientMap , float minimalCoincidenceValue) {
        this(srcFile, minimalCoincidenceValue);
        this.attributeCoefficientMap = attributeCoefficientMap;
    }

    @Override
    public XLog proceed() throws LogParsingError {
        try {
            XesXmlParser xUniversalParser = new XesXmlParser();
            if (xUniversalParser.canParse(srcFile)) {
                List<XLog> parsedLog = xUniversalParser.parse(srcFile);
                if (traceValidator.validateIsEmpty(parsedLog)) return  new XLogImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class));
                resultLog = buildTracesBasedOnInvariants(parsedLog, minimalCoincidenceValue, attributeCoefficientMap);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if (resultLog == null) {
            throw new LogParsingError("Result log is empty. Impossible to build output file");
        }
        return resultLog;
    }

    private XLog buildTracesBasedOnInvariants(List<XLog> parsedLog, float minimalCoincidenceValue, Map<String, Float> attributeCoefficientMap) {
        resultLog = new XLogImpl(parsedLog.get(0).getAttributes());
        for (XEvent event : parsedLog.get(0).get(0)) {
            insertEventInLogByCriteria(resultLog, event, false, minimalCoincidenceValue, attributeCoefficientMap);
        }
        return resultLog;
    }

    private void insertEventInLogByCriteria(XLog xLog, XEvent xEvent, boolean deepSearchByAllEvents, float minimalCoincidenceValue, Map<String, Float> attributeCoefficientMap) {
        // Insert first event if result log is empty
        if (proceedEventForEmptyResultLog(xLog, xEvent)) return;

        // Search for better coincidence
        Map<Integer, Float> coincidencesMap = buildCoincidenceMapForEvent(xLog, xEvent, deepSearchByAllEvents, attributeCoefficientMap);
        Pair<Integer, Float> traceIndexCoincidenceValue = getKeyOfBestMatchValue(coincidencesMap);

//        if (traceIndexCoincidenceValue.getValue() == ZERO_COINCIDENCE_VALUE) {
//            if (!deepSearchByAllEvents) {
//                insertEventInLogByCriteria(xLog, xEvent, true, minimalCoincidenceValue);
//            }
//        }

//        System.out.println("coincidencesMap:" + coincidencesMap + "   traceIndexCoincidenceValue:" + traceIndexCoincidenceValue);
        // Insert value in a trace with highest coincidence
        if (coincidencesMap.get(traceIndexCoincidenceValue.getKey()) >= minimalCoincidenceValue) {
            resultLog.get(traceIndexCoincidenceValue.getKey()).add(xEvent);
        } else {
            XTraceImpl trace = new XTraceImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class));
            resultLog.add(trace);
            trace.add(xEvent);
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

    private Map<Integer, Float> buildCoincidenceMapForEvent(XLog xLog, XEvent xEvent, boolean deepSearchByAllEvents, Map<String, Float> attributeCoefficientMap) {
        Map<Integer, Float> coincidencesMap;
//        if (deepSearchByAllEvents) {
//            coincidencesMap = getCoincidencesByInvariants(xLog, xEvent);
//        } else {
        coincidencesMap = getCoincidencesForLastEventsInTraces(xLog, xEvent, attributeCoefficientMap);
//        }
        return coincidencesMap;
    }

    private Map<Integer, Float> getCoincidencesForLastEventsInTraces(XLog xLog, XEvent xEvent, Map<String, Float> attributeCoefficientMap) {
        Map<Integer, Float> resultMap = new HashMap<>();
        for (int i = 0; i < xLog.size(); i++) {
            XTrace trace = xLog.get(i);
            XEvent lastEventOfTrace = trace.get(trace.size() - 1);
            float coensidenceValue = ValidationFactory.maxAttributesCoincidenceWeightCriteria(lastEventOfTrace, xEvent, attributeCoefficientMap);
            resultMap.put(i, coensidenceValue);
        }
        return resultMap;
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

    private Pair<Integer, Float> getKeyOfBestMatchValue(Map<Integer, Float> coincidencesMap) {
        Float firstValueInMap = coincidencesMap.get(0);
        int maxValueIndex = 0;
        float currentMaxValue = firstValueInMap;
        Float[] coincidences = coincidencesMap.values().toArray(new Float[coincidencesMap.size()]);

        for (int i = 0; i < coincidences.length; i++) {
            if (currentMaxValue < coincidences[i]) {
                maxValueIndex = i;
                currentMaxValue = coincidences[i];
            }
        }

        return new Pair<>(maxValueIndex, currentMaxValue);
    }
}
