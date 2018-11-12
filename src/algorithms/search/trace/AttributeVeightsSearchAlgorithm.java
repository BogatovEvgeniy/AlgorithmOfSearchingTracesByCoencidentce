package algorithms.search.trace;

import algorithms.ILogAlgorithm;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Tha algorithm assumes that his input is an set of event in an one trace.
 * So here we are assuming that we are working with unstructured log.
 * In {@link #proceed(XLog)} method first of all will be called {@link #checkLog(XLog)} method,
 * which will throw exception in case of the log contains more then one trace
 */

public class AttributeVeightsSearchAlgorithm implements ILogAlgorithm {

    public static final float VALUE_IS_EQUAL = 1F;
    public static final float VALUE_IS_DIFFERENT = 0F;
    private int stepSize;
    private int maxAllowedFails;
    private float minimalCoinsidece;
    private Set<Pair<Integer, Integer>> rangeSet;
    private List<List<String>> attributeSets;
    private List<Map<String, Float>> coincidenceForEachAttributeInSet = new ArrayList<>();

    public AttributeVeightsSearchAlgorithm(int stepSize,
                                           int maxAllowedFails,
                                           float minimalCoinsidece,
                                           Set<Pair<Integer, Integer>> rangeSet,
                                           List<List<String>> attributeSets) {

        this.stepSize = stepSize;
        this.maxAllowedFails = maxAllowedFails;
        this.minimalCoinsidece = minimalCoinsidece;
        this.rangeSet = rangeSet;
        this.attributeSets = attributeSets;
    }

    @Override
    public XLog proceed(XLog originLog) {
        checkLog(originLog);
        for (List<String> attributeSet : attributeSets) { // Attributes
            for (Pair<Integer, Integer> firstLastIndexOfRange : rangeSet) {
                // Get the first index of list due we have checked it before
                List<XEvent> eventRange = originLog.get(0).subList(firstLastIndexOfRange.getKey(), firstLastIndexOfRange.getValue());
                Map<String, Float> rangeCoincidence = coincidenceInRange(eventRange, attributeSet);
                if (rangeCoincidence == null){
                    break;
                }
                coincidenceForEachAttributeInSet.add(rangeCoincidence);
            }
        }

        return null;
    }

    private Map<String, Float> coincidenceInRange(List<XEvent> eventRange, List<String> attributeSet) {
        Map<String, Float> attributeSetCoincidenceOnRange = new HashMap<>();
        int negativeTriesCounter = 0;
        for (int lastEventIndexInStep = 0; lastEventIndexInStep < eventRange.size(); lastEventIndexInStep += stepSize) {
            List<XEvent> inStepEvents = eventRange.subList(lastEventIndexInStep, lastEventIndexInStep + lastEventIndexInStep);
            Map<String, Float> stepCoincidence = calculateCoincidenceInStep(attributeSet, inStepEvents);

            if (stepCoincidence == null) {
                negativeTriesCounter ++;
            } else {
                negativeTriesCounter = 0;
            }

            if (negativeTriesCounter > maxAllowedFails){
                return null;
            }
            else {
                if (attributeSetCoincidenceOnRange.size() == 0) {
                    attributeSetCoincidenceOnRange = stepCoincidence;
                } else {
                    attributeSetCoincidenceOnRange = mergeTwoMapsWithAverageValueInResult(stepCoincidence, attributeSetCoincidenceOnRange);
                }
            }
        }
        return attributeSetCoincidenceOnRange;
    }

    private Map<String, Float> calculateCoincidenceInStep(List<String> attributeSet, List<XEvent> inStepEvents) {
        Map<String, Float> coincidenceInStep = new HashMap<>();
        for (int inStepEventIndex = 0; inStepEventIndex < inStepEvents.size() - 1; inStepEventIndex++) {
            Map<String, Float> attributeCoincidence = calculateCoincidenceEventPair(attributeSet, inStepEvents, inStepEventIndex);
            coincidenceInStep = mergeTwoMapsWithAverageValueInResult(coincidenceInStep, attributeCoincidence);
        }

        coincidenceInStep = Maps.transformValues(coincidenceInStep, value-> value / attributeSet.size());
        coincidenceInStep = Maps.filterEntries(coincidenceInStep, value -> Float.compare(value.getValue(), minimalCoinsidece) > 0);
        return coincidenceInStep;
    }

    private Map<String, Float> calculateCoincidenceEventPair(List<String> attributeSet, List<XEvent> inStepEvents, int inStepEventIndex) {
        Map<String, Float> resultMap = new HashMap<>();
        for (String attributeKey : attributeSet) {
            resultMap.put(attributeKey, VALUE_IS_DIFFERENT);
            Object currentInStepEventAttr = inStepEvents.get(inStepEventIndex).getAttributes().get(attributeKey);
            Object nextInStepEventAttr = inStepEvents.get(inStepEventIndex + 1).getAttributes().get(attributeKey);
            if (currentInStepEventAttr.equals(nextInStepEventAttr)) {
                resultMap.put(attributeKey, VALUE_IS_EQUAL);
            }
        }
        return resultMap;
    }

    private Map<String, Float> mergeTwoMapsWithAverageValueInResult(Map<String, Float> sourceMap, Map<String, Float> destinationMap
    ) {
        return Stream.concat(sourceMap.entrySet().stream(), destinationMap.entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(), // The key
                        entry -> entry.getValue(), // The value
                        // The "merger"
                        (firstEntry, secondEntry) -> (firstEntry + secondEntry) / 2
                        )
                );
    }


    private void checkLog(XLog originLog) {
        if (originLog == null || originLog.size() < 1 || originLog.size() > 1) {
            throw new IllegalArgumentException("The log should be unstructured log? not NULL and contains only one trace with events");
        }
    }
}

