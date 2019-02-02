package algorithms.search.trace;

import algorithms.ILogAlgorithm;
import algorithms.Utils;
import io.db.DBWriter;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import utils.AttributeUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Tha algorithm assumes that his input is an set of event in an one trace.
 * So here we are assuming that we are working with unstructured log.
 * In {@link #proceed(XLog)} method first of all will be called {@link #checkLog(XLog)} method,
 * which will throw exception in case of the log contains more then one trace
 * <p>
 * <p>
 * 1. Define set of events / Define window size
 * 2. Define events in a window
 * 3. Define attributeSets per Window
 * 4. Calculate window coincidence
 * 5. Store events with coincidence and window index (KEY,VAL -> window_index, values)
 * 6. Store in DB
 * 7. IF (window_size  + window_first_event_index < log_events_count) THEN go to 8   ELSE go to 9
 * 8. Move window one event down
 * 9. Repeat 4 - 7
 * 10. Calculate average value for all stored data on the 5th step
 * 11. Store results in DB
 * 12. Return results for print as a list of objects {@link AttributeSetWeightPerRanges} : values|keys|ranges|weights_per_range|summary_weight
 */

public abstract class BaseWeightSearchAlgorithm implements ILogAlgorithm<List<AttributeSetWeightPerRanges>> {

    public static final int FAIL_COUNT_UNLIMITED = -1;
    private final DBWriter dbWriter;
    List<List<String>> attributeSets;
    private int windowSize;
    private int maxAllowedFails;
    private float minimalCoincidence;
    private List<AttributeSetWeightPerRanges> coincidenceForEachAttributeInSet = new LinkedList<>();
    private int windowIndex;
    private XLog originLog;


    // TODO Replace minimalCoincidence. Minimal coincidence through function or through listener. Window or Log or Range -> CoincidenceProcessor
    public BaseWeightSearchAlgorithm(int windowSize, int maxAllowedFails, float minimalCoincidence) {
        /**
         *1. Define window size
         */
        this.windowSize = windowSize;
        this.maxAllowedFails = maxAllowedFails;
        this.minimalCoincidence = minimalCoincidence;
        dbWriter = DBWriter.init();
    }


    /**
     * 1. Define set of events
     */
    @Override
    public List<AttributeSetWeightPerRanges> proceed(XLog originLog) {
        this.originLog = originLog;
        checkLog(originLog);
//        while (moreEventsAvailable(originLog, windowIndex)) {
//
//            /**
//             *  2. Define events in a window
//             */
//            int lastWindowEvent = windowIndex + windowSize;
//            List<XEvent> eventRange = originLog.get(0).subList(windowIndex, lastWindowEvent);
//
//            /**
//             * 3. Define attributeSets per Window
//             */
//            attributeCoincidence(originLog, eventRange);
//
//            /**
//             *  8. Move window one event down
//             */
//            windowIndex++;
//            System.out.println("Index:" + windowIndex);
//        }

        /**
         *  10. Calculate average value for all stored data on the 5th step
         */
        calculateWeightsTable(originLog);

        /**
         * Return results for print
         */
        return coincidenceForEachAttributeInSet;
    }

    private void calculateWeightsTable(XLog originLog) {
        dbWriter.storeAttributeSets(attributeSets);

        try {
            for (int attrSetIndex = 11; attrSetIndex < attributeSets.size(); attrSetIndex++) {
                List<String> attributes = getAttrForIndex(attrSetIndex);
                int rangeSize = originLog.get(0).size();
                List<XEvent> valueSetsPerAttr = getValuesForAttrIndex(attrSetIndex, attributes, 0, rangeSize);
                dbWriter.storeValueSets(attrSetIndex, valueSetsPerAttr);

                for (XEvent xEvent : valueSetsPerAttr) {
                    List<Integer> rangeIndexes = dbWriter.getRangeSetPerValueSet(attrSetIndex, xEvent.getAttributes(), attributes);
                    AttributeSetWeightPerRanges weightPerRanges = calculateWeights(attributes, attrSetIndex, rangeIndexes, xEvent.getAttributes());
                    coincidenceForEachAttributeInSet.add(weightPerRanges);

                    /**
                     * 11. Store results in DB
                     */
                    dbWriter.storeWeightCalculations(attrSetIndex, weightPerRanges);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    private int[] fillArrayOfInts(int i, int rangeSize) {
        int[] result = new int[rangeSize];

        for (int rangeIndex = 0; rangeIndex < rangeSize; rangeIndex++) {
            result[rangeIndex] = rangeIndex;
        }

        return result;
    }

    private List<String> getAttrForIndex(int attrSetIndex) throws SQLException {
        return dbWriter.getAttrsPerAttrSet(attrSetIndex);
    }

    private List<XEvent> getValuesForAttrIndex(int attrSetIndex, List<String> attributes, int fromRangeIndex, int toRangeIndex) throws SQLException {
        return dbWriter.getValueSetsPerAttrSet(attrSetIndex, attributes, fromRangeIndex, toRangeIndex);
    }

    private List<XEvent> getEventList(int attrSetIndex, int rangeId) throws SQLException {
        return dbWriter.getEventsPerAttrSet(attrSetIndex, rangeId);
    }

    private AttributeSetWeightPerRanges calculateWeights(List<String> attributes, int attrSetIndex, List<Integer>  ranges, XAttributeMap valueSetPerAttr) throws SQLException {

        float sumOfWeights = 0;
        int comparedVals = 0;
        Map<Integer, Float> rangesUsedInCalculation = new TreeMap<>();

        for (int range : ranges) {
            List<XEvent> eventList = getEventList(attrSetIndex, range);
            boolean isWindowContainsValuesSet = AttributeUtils.eventListContainsEqualValues(eventList, valueSetPerAttr);

            if (isWindowContainsValuesSet) {
                float rangeWeight = calculateWeightPerStep(eventList, attributes);
                sumOfWeights += rangeWeight;
                comparedVals++;
                rangesUsedInCalculation.put(range, rangeWeight);
            }
        }

        return new AttributeSetWeightPerRanges(Utils.sortMap(rangesUsedInCalculation),
                valueSetPerAttr, sumOfWeights / getEventsCountInLog());
    }

    // Here we assume that the log is one big set of events
    private int getEventsCountInLog() {
        return originLog.get(0).size();
    }

    private float calculateWeightPerStep(List<XEvent> events, List<String> attributes) {
        float coincidenceInWindow = 0f;
        int countOfComparision = 0;
        for (int firstComparisonValIndex = 0; firstComparisonValIndex < events.size() - 1; firstComparisonValIndex++) {
            // Here were added one more cycle to be able compare each event with the other in the step
            for (int secondComparisionValIndex = firstComparisonValIndex + 1; secondComparisionValIndex < events.size(); secondComparisionValIndex++) {
                XAttributeMap firstEventAttributes = events.get(firstComparisonValIndex).getAttributes();
                XAttributeMap secondEventAttributes = events.get(secondComparisionValIndex).getAttributes();
                coincidenceInWindow += calculateCoincidenceEventPair(attributes, firstEventAttributes, secondEventAttributes);
                countOfComparision ++;
            }
        }

        if (coincidenceInWindow > minimalCoincidence) {
            return coincidenceInWindow/countOfComparision;
        } else {
            return 0f;
        }
    }


    private float calculateCoincidenceEventPair(List<String> attributeSet, XAttributeMap currentInStepEventAttr, XAttributeMap nextInStepEventAttr) {
        float attributeCoincidence = 0;
        for (String attributeKey : attributeSet) {
            if (currentInStepEventAttr.get(attributeKey) != null && nextInStepEventAttr.get(attributeKey) != null
                    && currentInStepEventAttr.get(attributeKey).equals(nextInStepEventAttr.get(attributeKey))) {
                attributeCoincidence++;
            }
        }

        return attributeCoincidence / attributeSet.size();
    }

    /**
     * 3. Define attributeSets per Window
     */
    private void attributeCoincidence(XLog originLog, List<XEvent> eventRange) {
        attributeSets = getAttributeSet(originLog, windowIndex, windowSize);

        for (int attrSetIndex = 0; attrSetIndex < attributeSets.size(); attrSetIndex++) {

            /**
             *  4. Calculate window coincidence
             */
            int negativeTriesCounter = 0;
            float windowCoincidence = windowCoincidence(attrSetIndex, attributeSets.get(attrSetIndex), eventRange);

            if (windowCoincidence == 0) {
                negativeTriesCounter++;
            } else {
                negativeTriesCounter = 0;
            }

            if (maxAllowedFails != FAIL_COUNT_UNLIMITED && negativeTriesCounter > maxAllowedFails) {
                return;
            }
        }
    }

    /**
     * 7. IF (window_size  + window_first_event_index < log_events_count) THEN go to 8   ELSE go to 9
     */
    private boolean moreEventsAvailable(XLog originLog, int windowIndex) {
        return originLog.get(0).size() > windowIndex + windowSize; // Assumption that we have a log with only one trace
    }

    /**
     * 4. Calculate window coincidence
     */
    float windowCoincidence(int attributeSetIndex, List<String> attributeSet, List<XEvent> inStepEvents) {
        float coincidenceInStep = 0f;
        int stepCounter = 0;
        boolean isEqualsFound = false;
        for (int firstComparisonValIndex = 0; firstComparisonValIndex < inStepEvents.size() - 1; firstComparisonValIndex++) {
            int secondComparisionValIndex = firstComparisonValIndex + 1;
            XEvent currEvent = inStepEvents.get(firstComparisonValIndex);
            XAttributeMap currentInStepEventAttr = currEvent.getAttributes();
            XEvent nextEvent = inStepEvents.get(secondComparisionValIndex);
            XAttributeMap nextInStepEventAttr = nextEvent.getAttributes();
            float attributeSetCoincidence = calculateCoincidenceEventPair(attributeSet, currentInStepEventAttr, nextInStepEventAttr);
            if (attributeSetCoincidence > 0) {
                isEqualsFound = true;
                /**
                 * 5. Store events with coincidence and window index (KEY,VAL -> window_index, values)
                 */
                dbWriter.insertPairOfEvents(attributeSet,
                        windowIndex, attributeSetIndex,
                        windowIndex + firstComparisonValIndex, windowIndex + secondComparisionValIndex,
                        currEvent, nextEvent);
                coincidenceInStep++;
            }
            stepCounter++;
        }

        if (isEqualsFound) {
            coincidenceInStep = coincidenceInStep / stepCounter;

            if (coincidenceInStep > minimalCoincidence) {
                return coincidenceInStep;
            } else {
                return 0f;
            }
        } else {
            return 0f;
        }
    }

    void checkLog(XLog originLog) {
        if (originLog == null || originLog.size() < 1 || originLog.size() > 1) {
            throw new IllegalArgumentException("The log should be unstructured log: not NULL and contains only one trace with events");
        }
    }


    abstract List<List<String>> getAttributeSet(XLog log, int windowIndex, int windowSize);
}
