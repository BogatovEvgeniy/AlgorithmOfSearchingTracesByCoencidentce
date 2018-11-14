package algorithms.search.trace;

import algorithms.ILogAlgorithm;
import javafx.util.Pair;
import org.deckfour.xes.model.XAttributeMap;
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

public class AttributeWeightsSearchAlgorithm implements ILogAlgorithm<List<AttributeWeightsSearchAlgorithm.AttributeSetCoincidenceOnRange>> {

    public static final int FAIL_COUNT_UNLIMITED = -1;
    private int stepSizeInRange;
    private int maxAllowedFails;
    private float minimalCoinsidece;
    private Set<Pair<Integer, Integer>> rangeSet;
    private List<List<String>> attributeSets;
    private List<AttributeSetCoincidenceOnRange> coincidenceForEachAttributeInSet = new ArrayList<>();

    public AttributeWeightsSearchAlgorithm(int stepSizeInRange,
                                           int maxAllowedFails,
                                           float minimalCoinsidece,
                                           Set<Pair<Integer, Integer>> rangeSet,
                                           List<List<String>> attributeSets) {

        this.stepSizeInRange = stepSizeInRange;
        this.maxAllowedFails = maxAllowedFails;
        this.minimalCoinsidece = minimalCoinsidece;
        this.rangeSet = rangeSet;
        this.attributeSets = attributeSets;

        System.out.println("Config:" + this.toString());
    }

    @Override
    public List<AttributeSetCoincidenceOnRange> proceed(XLog originLog) {
        checkLog(originLog);
        for (List<String> attributeSet : attributeSets) { // Attributes
            for (Pair<Integer, Integer> firstLastIndexOfRange : rangeSet) {
                // Get the first index of list due we have checked it before
                List<XEvent> eventRange = originLog.get(0).subList(firstLastIndexOfRange.getKey(), firstLastIndexOfRange.getValue());
                Float rangeCoincidence = coincidenceInRange(eventRange, attributeSet);

                coincidenceForEachAttributeInSet.add(new AttributeSetCoincidenceOnRange(attributeSet, firstLastIndexOfRange, rangeCoincidence));
            }
        }

        return coincidenceForEachAttributeInSet;
    }

    private float coincidenceInRange(List<XEvent> eventRange, List<String> attributeSet) {
        float attributeSetCoincidenceOnRange = 0f;
        int negativeTriesCounter = 0;
        for (int lastEventIndexInStep = 0; eventRange.size() - lastEventIndexInStep > stepSizeInRange; lastEventIndexInStep += stepSizeInRange) {
            List<XEvent> inStepEvents = eventRange.subList(lastEventIndexInStep, lastEventIndexInStep + stepSizeInRange);
            Float stepCoincidence = calculateCoincidenceInStep(attributeSet, inStepEvents);

            if (stepCoincidence == 0) {
                negativeTriesCounter++;
            } else {
                negativeTriesCounter = 0;
            }

            if (maxAllowedFails != FAIL_COUNT_UNLIMITED && negativeTriesCounter > maxAllowedFails) {
                return 0;
            } else {
                attributeSetCoincidenceOnRange += stepCoincidence;
            }
        }

        attributeSetCoincidenceOnRange = attributeSetCoincidenceOnRange / (eventRange.size() / stepSizeInRange);
        return attributeSetCoincidenceOnRange;
    }

    private float calculateCoincidenceInStep(List<String> attributeSet, List<XEvent> inStepEvents) {
        float coincidenceInStep = 0f;
        int stepCounter = 0;
        for (int firstComparisonValIndex = 0; firstComparisonValIndex < inStepEvents.size() - 1; firstComparisonValIndex++) {
            // Here were added one more cycle to be able compare each event with the other in the step
            for (int secondComparisionValIndex = firstComparisonValIndex + 1; secondComparisionValIndex < inStepEvents.size(); secondComparisionValIndex++) {
                boolean isEquals = calculateCoincidenceEventPair(attributeSet, inStepEvents, firstComparisonValIndex, secondComparisionValIndex);
                if (isEquals) {
                    coincidenceInStep++;
                }
                stepCounter++;
            }
        }

        int finalStepCounter = stepCounter;
        coincidenceInStep = coincidenceInStep / finalStepCounter;

        if (coincidenceInStep > minimalCoinsidece) {
            return coincidenceInStep;
        } else {
            return 0f;
        }
    }

    private boolean calculateCoincidenceEventPair(List<String> attributeSet, List<XEvent> inStepEvents, int firstComparisonValIndex, int secondComparisionValIndex) {
        XAttributeMap currentInStepEventAttr = inStepEvents.get(firstComparisonValIndex).getAttributes();
        XAttributeMap nextInStepEventAttr = inStepEvents.get(secondComparisionValIndex).getAttributes();
        boolean containsAtLeastOneAttribute = true;

        for (String aKey : attributeSet) {
            if (!currentInStepEventAttr.keySet().contains(aKey)) {
                containsAtLeastOneAttribute = false;
                break;
            }
        }

        if (!containsAtLeastOneAttribute){
            return false;
        }

        for (String attributeKey : attributeSet) {
            if (currentInStepEventAttr.get(attributeKey) != null
                    &&!currentInStepEventAttr.get(attributeKey).equals(nextInStepEventAttr.get(attributeKey))) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Float> mergeTwoMapsWithSumInResult(Map<String, Float> sourceMap, Map<String, Float> destinationMap
    ) {
        return Stream.concat(sourceMap.entrySet().stream(), destinationMap.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // The key
                        Map.Entry::getValue, // The value
                        // The "merger"
                        (firstEntry, secondEntry) -> firstEntry + secondEntry
                        )
                );
    }

    private Map<String, Float> mergeTwoMapsWithAverageValueInResult(Map<String, Float> sourceMap, Map<String, Float> destinationMap, int divider
    ) {
        return Stream.concat(sourceMap.entrySet().stream(), destinationMap.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // The key
                        Map.Entry::getValue, // The value
                        // The "merger"
                        (firstEntry, secondEntry) -> (firstEntry + secondEntry) / divider
                        )
                );
    }


    private void checkLog(XLog originLog) {
        if (originLog == null || originLog.size() < 1 || originLog.size() > 1) {
            throw new IllegalArgumentException("The log should be unstructured log: not NULL and contains only one trace with events");
        }
    }

    public class AttributeSetCoincidenceOnRange {
        private List<String> attributeSet;
        private Pair<Integer, Integer> firstLastIndexOfRange;
        private Float rangeCoincidence;

        public AttributeSetCoincidenceOnRange(List<String> attributeSet, Pair<Integer, Integer> firstLastIndexOfRange, Float rangeCoincidence) {
            this.attributeSet = attributeSet;
            this.firstLastIndexOfRange = firstLastIndexOfRange;
            this.rangeCoincidence = rangeCoincidence;
        }

        @Override
        public String toString() {
            return "AttributeSetCoincidenceOnRange{" +
                    "attributeSet=" + attributeSet +
                    ", in a range [" + firstLastIndexOfRange.getKey() + "..." + firstLastIndexOfRange.getValue() + "]" +
                    ", rangeCoincidence=" + rangeCoincidence +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AttributeWeightsSearchAlgorithm{" +
                "stepSizeInRange=" + stepSizeInRange +
                ", maxAllowedFails=" + maxAllowedFails +
                ", minimalCoinsidece=" + minimalCoinsidece +
                ", rangeSet=" + rangeSet +
                ", attributeSets=" + attributeSets +
                '}';
    }
}

