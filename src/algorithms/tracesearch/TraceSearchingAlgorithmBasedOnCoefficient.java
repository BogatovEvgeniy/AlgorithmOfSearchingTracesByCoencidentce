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
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
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
    private final File resFile;
    private Map<String, Float> correctionAtributesMap;
    private float minimalCoincidenceValue;

    private List<XEvent> undefinedEvents = new ArrayList<>();
    private XLog resultLog;

    public TraceSearchingAlgorithmBasedOnCoefficient(File srcFile, File resFile, float minimalCoincidenceValue) {
        this.srcFile = srcFile;
        this.resFile = resFile;
        this.minimalCoincidenceValue = minimalCoincidenceValue;
    }

    public TraceSearchingAlgorithmBasedOnCoefficient(File srcFile, File resFile, Map<String, Float> correctionAttributesMap, float minimalCoincidenceValue) {
        this(srcFile, resFile, minimalCoincidenceValue);
        this.correctionAtributesMap = correctionAttributesMap;
    }

    @Override
    public void proceed() {
        try {
            XesXmlParser xUniversalParser = new XesXmlParser();
            if (xUniversalParser.canParse(srcFile)) {
                List<XLog> parsedLog = xUniversalParser.parse(srcFile);
                if (validateLog(parsedLog)) return;
                Map<String, Float> attributeCoefficientMap = prepareCoefficientMap(parsedLog);
                resultLog = buildTracesBasedOnInvariants(parsedLog, minimalCoincidenceValue, attributeCoefficientMap);

                if (resultLog == null) {
                    throw new LogParsingError("Result log is empty. Impossible to build output file");
                }

                new XesXmlSerializer().serialize(resultLog, new FileOutputStream(resFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Float> prepareCoefficientMap(List<XLog> parsedLog) {
        Map<String, Float> attributeCoefficientMap = buildCoefficientMapForAttributes(parsedLog);
        attributeCoefficientMap = coefficientsCorrectionBaseOnIncomeData(correctionAtributesMap, attributeCoefficientMap);
        attributeCoefficientMap = rebalanceCoefficientsToValue(1, attributeCoefficientMap);
        return attributeCoefficientMap;
    }

    private Map<String, Float> rebalanceCoefficientsToValue(float targetValue, Map<String, Float> attributeCoefficientMap) {
        Iterator<String> iterator = attributeCoefficientMap.keySet().iterator();
        float coefficientSum = 0;
        while (iterator.hasNext()){
            coefficientSum += attributeCoefficientMap.get(iterator.next());
        }

        if (targetValue == coefficientSum) return attributeCoefficientMap;


        /**
         * The correction value calculates basing on expression below
         * t - target value usually equals to 1
         * n1...ni - the sum of coefficients in the map
         * x - the correction value used to make values in map that sum of them was equals target val
         *
         * t = x + (n1...ni)
         *
         * x = t - (ni...ni)
         *
         * t = (n1...ni)(x/(n1...ni) + 1)
         *
         */

        Map<String, Float> correctedMap = new HashMap<>();
        // calculate (x/(n1...ni) + 1)
        float correctionValue = ((targetValue - coefficientSum) / coefficientSum) + 1;

        iterator = attributeCoefficientMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            correctedMap.put(key, attributeCoefficientMap.get(key) * correctionValue);
        }

        return correctedMap;
    }

    private Map<String, Float> coefficientsCorrectionBaseOnIncomeData(Map<String, Float> correctionAtributesMap, Map<String, Float> attributeCoefficientMap) {
        Map<String, Float> resultMap = new HashMap<>();
        Iterator<String> iterator = attributeCoefficientMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            float value = attributeCoefficientMap.get(key);
            if (correctionAtributesMap.containsKey(key)) {
                value = correctionAtributesMap.get(key) * attributeCoefficientMap.get(key);
            }
            resultMap.put(key, value);
        }

        return resultMap.size() > 0 ? resultMap : attributeCoefficientMap;
    }

    private Map<String, Float> buildCoefficientMapForAttributes(List<XLog> parsedLog) {
        XTrace trace = parsedLog.get(0).get(0);
        XAttributeMap attributes = trace.get(0).getAttributes();
        Map<String, List<Pair<String, Integer>>> valuesMap = fillAttributeValuesMap(parsedLog, attributes);
        return calculateCoefficientMapForEachAttribute(valuesMap, trace.size(), attributes.size());
    }


    private Map<String, Float> calculateCoefficientMapForEachAttribute(Map<String, List<Pair<String, Integer>>> valuesMap, int eventsInLog, int attributesPerEvent) {
        Map<String, Float> resultMap = new HashMap<>();
        Map<String, Float> varietyPerAttrMap = new HashMap<>();

        float sumOfVarieties = 0;
        for (String attributeName : valuesMap.keySet()) {
            List<Pair<String, Integer>> pairs = valuesMap.get(attributeName);
            float varietyOfEventsPerAttribute = (float) pairs.size()/eventsInLog;
            varietyPerAttrMap.put(attributeName, varietyOfEventsPerAttribute);
            sumOfVarieties += varietyOfEventsPerAttribute;
        }

        float variatePercentMultilayer = 1 / sumOfVarieties;
        for (String attributeName : valuesMap.keySet()) {
            resultMap.put(attributeName, (varietyPerAttrMap.get(attributeName) * variatePercentMultilayer));
        }

        return resultMap;
    }

    private Map<String, List<Pair<String, Integer>>> fillAttributeValuesMap(List<XLog> parsedLog, XAttributeMap attributes) {
        Map<String, List<Pair<String, Integer>>> valuesMap = new HashMap<>();
        for (String key : attributes.keySet()) {
            if (key.contains("timestamp")) break;
            for (XLog log : parsedLog) {
                for (XTrace trace : log) {
                    for (XEvent xEvent : trace) {
                        String attrValue = String.valueOf(xEvent.getAttributes().get(key));
                        if (!valuesMap.containsKey(key)) {
                            ArrayList<Pair<String, Integer>> value = new ArrayList<>();
                            valuesMap.put(key, value);
                            value.add(new Pair<String, Integer>(attrValue, 1));
                        } else {
                            List<Pair<String, Integer>> attributeValueFrequencyList = valuesMap.get(key);
                            int positionToInsert = -1;
                            for (int i = 0; i < attributeValueFrequencyList.size(); i++) {
                                if (attributeValueFrequencyList.get(i).getKey().equals(attrValue)) {
                                    positionToInsert = i;
                                    break;
                                }
                            }
                            if (positionToInsert >= 0) {
                                Integer currentAttrFrequency = attributeValueFrequencyList.get(positionToInsert).getValue().intValue();
                                attributeValueFrequencyList.remove(positionToInsert);
                                attributeValueFrequencyList.add(positionToInsert, new Pair<>(attrValue, currentAttrFrequency + 1));
                            } else {
                                attributeValueFrequencyList.add(new Pair<>(attrValue, 1));
                            }
                        }
                    }
                }
            }
        }
        return valuesMap;
    }

    private XLog  buildTracesBasedOnInvariants(List<XLog> parsedLog, float minimalCoincidenceValue, Map<String, Float> attributeCoefficientMap) {
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

    private boolean validateLog(List<XLog> parsedLog) {
        // If there are no resultLog, trace or event nothing will be written in file
        if (parsedLog.size() == 0) return true;
        if (parsedLog.get(0).size() == 0) return true;
        if (parsedLog.get(0).get(0).size() == 0) return true;
        return false;
    }
}
