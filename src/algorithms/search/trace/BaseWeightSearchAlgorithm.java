package algorithms.search.trace;

import algorithms.ILogAlgorithm;
import io.db.DBWriter;
import javafx.util.Pair;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
 * 7. IF (window_size  + window_first_event_index < log_events_count) THEN go to 7 ELSE go to 9
 * 8. Move window one event down
 * 9. Repeat 4 - 7
 * 10. Calculate average value for all stored data on the 5th step
 */

public abstract class BaseWeightSearchAlgorithm implements ILogAlgorithm<List<BaseWeightSearchAlgorithm.AttributeSetCoincidenceOnRange>> {

    public static final int FAIL_COUNT_UNLIMITED = -1;
    private final DBWriter dbWriter;
    List<List<String>> attributeSets;
    private int windowSize;
    private int maxAllowedFails;
    private float minimalCoincidence;
    private List<AttributeSetCoincidenceOnRange> coincidenceForEachAttributeInSet = new ArrayList<>();
    private int windowIndex;


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
    public List<AttributeSetCoincidenceOnRange> proceed(XLog originLog) {
        checkLog(originLog);
        while (moreEventsAvailable(originLog, windowIndex)) {

            /**
             *  2. Define events in a window
             */
            int lastWindowEvent = windowIndex + windowSize;
            List<XEvent> eventRange = originLog.get(0).subList(windowIndex, lastWindowEvent);

            /**
             * 3. Define attributeSets per Window
             */
            attributeSets = getAttributeSet(originLog, windowIndex, windowSize);
            for (List<String> attributeSet : attributeSets) {
                /**
                 *  4. Calculate window coincidence
                 */
                Float rangeCoincidence = coincidenceInRange(eventRange, attributeSet);
                coincidenceForEachAttributeInSet.add(new AttributeSetCoincidenceOnRange(attributeSet, new Pair<>(windowIndex, lastWindowEvent), rangeCoincidence));
            }
            windowIndex += windowSize;
        }
        return coincidenceForEachAttributeInSet;
    }

    private boolean moreEventsAvailable(XLog originLog, int windowIndex) {
        return originLog.get(0).size() > windowIndex + windowSize; // Assumption that we have a log with only one trace
    }

    float coincidenceInRange(List<XEvent> windowEvents, List<String> attributeSet) {
        float attributeSetCoincidenceOnRange = 0f;
        int negativeTriesCounter = 0;
        Float stepCoincidence = calculateCoincidenceInStep(attributeSet, windowEvents);

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

        attributeSetCoincidenceOnRange = attributeSetCoincidenceOnRange / (windowEvents.size() / windowSize);
        return attributeSetCoincidenceOnRange;
    }

    float calculateCoincidenceInStep(List<String> attributeSet, List<XEvent> inStepEvents) {
        float coincidenceInStep = 0f;
        int stepCounter = 0;
        for (int firstComparisonValIndex = 0; firstComparisonValIndex < inStepEvents.size() - 1; firstComparisonValIndex++) {
            // Here were added one more cycle to be able compare each event with the other in the step
            for (int secondComparisionValIndex = firstComparisonValIndex + 1; secondComparisionValIndex < inStepEvents.size(); secondComparisionValIndex++) {
                XEvent currEvent = inStepEvents.get(firstComparisonValIndex);
                XAttributeMap currentInStepEventAttr = currEvent.getAttributes();
                XEvent nextEvent = inStepEvents.get(secondComparisionValIndex);
                XAttributeMap nextInStepEventAttr = nextEvent.getAttributes();
                boolean isEquals = calculateCoincidenceEventPair(attributeSet, currentInStepEventAttr, nextInStepEventAttr);
                if (isEquals) {
                    /**
                     * 5. Store events with coincidence and window index (KEY,VAL -> window_index, values)
                     */
                    dbWriter.insertEvents(currEvent, nextEvent);
                    coincidenceInStep++;
                }
                stepCounter++;
            }
        }

        int finalStepCounter = stepCounter;
        coincidenceInStep = coincidenceInStep / finalStepCounter;

        if (coincidenceInStep > minimalCoincidence) {
            return coincidenceInStep;
        } else {
            return 0f;
        }
    }

    boolean calculateCoincidenceEventPair(List<String> attributeSet, XAttributeMap currentInStepEventAttr, XAttributeMap nextInStepEventAttr) {
        boolean containsAtLeastOneAttribute = true;

        for (String aKey : attributeSet) {
            if (!currentInStepEventAttr.keySet().contains(aKey)) {
                containsAtLeastOneAttribute = false;
                break;
            }
        }

        if (!containsAtLeastOneAttribute) {
            return false;
        }

        for (String attributeKey : attributeSet) {
            if (currentInStepEventAttr.get(attributeKey) != null
                    && !currentInStepEventAttr.get(attributeKey).equals(nextInStepEventAttr.get(attributeKey))) {
                return false;
            }
        }
        return true;
    }

    void checkLog(XLog originLog) {
        if (originLog == null || originLog.size() < 1 || originLog.size() > 1) {
            throw new IllegalArgumentException("The log should be unstructured log: not NULL and contains only one trace with events");
        }
    }


    abstract List<List<String>> getAttributeSet(XLog log, int windowIndex, int windowSize);

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
        return "PredefibedAttributeWeightsSearchAlgorithm{" +
                "windowSize=" + windowSize +
                ", maxAllowedFails=" + maxAllowedFails +
                ", minimalCoincidence=" + minimalCoincidence +
                ", attributeSets=" + attributeSets +
                '}';
    }
}
