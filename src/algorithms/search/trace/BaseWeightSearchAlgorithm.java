package algorithms.search.trace;

import algorithms.ILogAlgorithm;
import io.db.DBWriter;
import javafx.util.Pair;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

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
 * 7. IF (window_size  + window_first_event_index < log_events_count) THEN go to 8   ELSE go to 9
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
            attributeCoincidence(originLog, lastWindowEvent, eventRange);

            /**
             *  8. Move window one event down
             */
            windowIndex ++;
            System.out.println("Index:" + windowIndex);
        }
        return coincidenceForEachAttributeInSet;
    }

    /**
     * 3. Define attributeSets per Window
     */
    private void attributeCoincidence(XLog originLog, int lastWindowEvent, List<XEvent> eventRange) {
        attributeSets = getAttributeSet(originLog, windowIndex, windowSize);
        float attributeSetCoincidenceInWindow = 0;

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
            } else {
                if (windowCoincidence > 0) {
                    attributeSetCoincidenceInWindow += windowCoincidence;
                }
            }
            coincidenceForEachAttributeInSet.add(new AttributeSetCoincidenceOnRange(attributeSets.get(attrSetIndex), attributeSetCoincidenceInWindow, windowIndex, lastWindowEvent));
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
                        windowIndex + firstComparisonValIndex,windowIndex + secondComparisionValIndex,
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

    private float calculateCoincidenceEventPair(List<String> attributeSet, XAttributeMap
            currentInStepEventAttr, XAttributeMap nextInStepEventAttr) {

      float attributeCoincidence = 0;
        for (String attributeKey : attributeSet) {
            if (currentInStepEventAttr.get(attributeKey) != null && nextInStepEventAttr.get(attributeKey) != null
                    && currentInStepEventAttr.get(attributeKey).equals(nextInStepEventAttr.get(attributeKey))) {
                attributeCoincidence ++;
            }
        }

        return attributeCoincidence/attributeSet.size();
    }

    void checkLog(XLog originLog) {
        if (originLog == null || originLog.size() < 1 || originLog.size() > 1) {
            throw new IllegalArgumentException("The log should be unstructured log: not NULL and contains only one trace with events");
        }
    }


    abstract List<List<String>> getAttributeSet(XLog log, int windowIndex, int windowSize);

    public class AttributeSetCoincidenceOnRange {
        private final float commonConcidence;
        private int windowIndex;
        private int lastIndex;
        private List<String> attributeSet;

        public AttributeSetCoincidenceOnRange(List<String> attributeSet, float commonCoincidence, int windowIndex, int lastIndex) {
            this.attributeSet = attributeSet;
            this.commonConcidence = commonCoincidence;
            this.windowIndex = windowIndex;
            this.lastIndex = lastIndex;
        }

        @Override
        public String toString() {
            return "AttributeSetCoincidenceOnRange{" +
                    "commonConcidence=" + commonConcidence +
                    ", windowIndex=" + windowIndex +
                    ", lastIndex=" + lastIndex +
                    ", attributeSet=" + attributeSet +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BaseWeightSearchAlgorithm{" +
                "windowSize=" + windowSize +
                ", maxAllowedFails=" + maxAllowedFails +
                ", minimalCoincidence=" + minimalCoincidence +
                ", attributeSets=" + attributeSets +
                '}';
    }
}
