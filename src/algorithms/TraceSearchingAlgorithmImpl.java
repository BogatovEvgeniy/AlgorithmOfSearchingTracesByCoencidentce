package algorithms;

import exceptions.LogParsingError;
import javafx.util.Pair;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * The algorithm of parsing logs consists only of events
 * Algorithm allows to group events by traces basing on maximal coinsidence of comparing events.
 * While algorithm proceed sets of events we were assumed that
 *  - the order of events reflects order of their execution in business - process of production
 *
 *
 *  The algorithm consists of the next steps
 *  - get the last event of the trace
 *  - start comparision of events one by one basing on maximal confident criteria
 *  - while the last event of the log was met or no coincidence was found
 *  - if no coincidence was found the event or group event will be copied in undefinedEvents list, till first cycle of events comparision was finished
 *  - if last event was met that next event will be copied in a new trace
 *  - when first cycle of events comparison was finished, that second cycle will be started, where each event from undefined events list is comparing with traces built on the previous cycle
 *
 */

public class TraceSearchingAlgorithmImpl implements ITraceSearchingAlgorithm {
    public static final int ZERO_COINCIDENCE_VALUE = 0;
    private final File srcFile;
    private final File resFile;
    private int minimalCoincidenceValue;

    private List<XEvent> undefinedEvents = new ArrayList<>();
    private XLog resultLog;

    public TraceSearchingAlgorithmImpl(File srcFile, File resFile, int minimalCoincidenceValue) {
        this.srcFile = srcFile;
        this.resFile = resFile;
        this.minimalCoincidenceValue = minimalCoincidenceValue;
    }

    @Override
    public void proceed() {
        try {
            XesXmlParser xUniversalParser = new XesXmlParser();
            List<XLog> parsedLog = null;
            if (xUniversalParser.canParse(srcFile)) {
                parsedLog = xUniversalParser.parse(srcFile);
                if (validateParceErrors(parsedLog)) return;
                resultLog = buildTracesBasedOnInvariants(parsedLog, minimalCoincidenceValue);

                if (resultLog == null) {
                    throw  new LogParsingError("Result log is empty. Impossible to build output file");
                }

                new XesXmlSerializer().serialize(resultLog, new FileOutputStream(resFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private XLog buildTracesBasedOnInvariants(List<XLog> parsedLog, int minimalCoincidenceValue) {
        resultLog = new XLogImpl(parsedLog.get(0).getAttributes());
        for (XEvent event: parsedLog.get(0).get(0)) {
            insertEventInLogByCriteria(resultLog, event, false, minimalCoincidenceValue);
        }
        return resultLog;
    }

    private void insertEventInLogByCriteria(XLog xLog, XEvent xEvent, boolean deepSearchByAllEvents, int minimalCoincidenceValue) {
        // Insert first event if result log is empty
        if (proceedEventForEmptyResultLog(xLog, xEvent)) return;

        // Search for better coincidence
        Map<Integer, Integer> coincidencesMap = buildCoincidenceMapForEvent(xLog, xEvent, deepSearchByAllEvents);
        Pair<Integer, Integer> traceIndexCoincidenceValue = getHigherValueKey(coincidencesMap);

        if (traceIndexCoincidenceValue.getValue() == ZERO_COINCIDENCE_VALUE) {
            if (!deepSearchByAllEvents) {
                insertEventInLogByCriteria(xLog, xEvent, true, minimalCoincidenceValue);
            }
        }

        System.out.println("coincidencesMap:" + coincidencesMap + "   traceIndexCoincidenceValue:" + traceIndexCoincidenceValue);
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

    private Map<Integer, Integer> buildCoincidenceMapForEvent(XLog xLog, XEvent xEvent, boolean deepSearchByAllEvents) {
        Map<Integer, Integer> coincidencesMap;
        if (deepSearchByAllEvents) {
            coincidencesMap = getCoincidencesByInvariants(xLog, xEvent);
        } else {
            coincidencesMap = getCoencidencesForLastEventsInTraces(xLog, xEvent);
        }
        return coincidencesMap;
    }

    private  Map<Integer, Integer> getCoencidencesForLastEventsInTraces(XLog xLog, XEvent xEvent) {
        Map<Integer, Integer> resultMap = new HashMap<>();

        for (int i = 0; i < xLog.size(); i++) {
            XTrace trace = xLog.get(i);
            XEvent lastEventOfTrace = trace.get(trace.size() - 1);
            int coensidenceValue = ValidationStrategy.maxAttributesCoincidences(lastEventOfTrace, xEvent);
            resultMap.put(i, coensidenceValue);
        }
        return resultMap;
    }

    private Map<Integer, Integer> getCoincidencesByInvariants(XLog xLog, XEvent xEvent) {
        Map<Integer, Integer> resultMap = new HashMap<>();
        for (int i = 0; i < xLog.size(); i++) {
            for (XEvent eventInTrace : xLog.get(i)) {
                int coincidenceValue = ValidationStrategy.maxAttributesCoincidences(eventInTrace, xEvent);
                if (!resultMap.containsKey(i) || (resultMap.containsKey(i) && resultMap.get(i) < coincidenceValue)) {
                    resultMap.put(i, coincidenceValue);
                }
            }
        }
        return resultMap;
    }

    private Pair<Integer,Integer> getHigherValueKey(Map<Integer, Integer> coincidencesMap) {
        Integer firstValueInMap = coincidencesMap.get(0);
        int maxValueIndex = 0;
        int currentMaxValue = firstValueInMap;
        Integer[] coincidences = coincidencesMap.values().toArray(new Integer[coincidencesMap.size()]);

        for (int i = 0; i < coincidences.length; i++) {
            if (currentMaxValue < coincidences[i]) {
                maxValueIndex = i;
                currentMaxValue = coincidences[i];
            }
        }

        return new Pair<>(maxValueIndex, currentMaxValue);
    }

    private boolean validateParceErrors(List<XLog> parsedLog) {
        // If there are no resultLog, trace or event nothing will be written in file
        if(parsedLog.size() == 0) return true;
        if(parsedLog.get(0).size() == 0) return true;
        if(parsedLog.get(0).get(0).size() == 0) return true;
        return false;
    }
}
