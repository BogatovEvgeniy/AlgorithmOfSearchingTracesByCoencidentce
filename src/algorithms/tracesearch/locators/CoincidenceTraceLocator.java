package algorithms.tracesearch.locators;

import algorithms.ValidationFactory;
import algorithms.tracesearch.ITraceSearchingAlgorithm;
import javafx.util.Pair;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.HashMap;
import java.util.Map;

public class CoincidenceTraceLocator implements ITraceSearchingAlgorithm.TraceLocator {
    private float minimalCoincidence;
    private Map<String, Float> attributesCoefficientMap;

    public CoincidenceTraceLocator(float minimalCoincidence, Map<String, Float> attributesCoefficientMap) {
        this.minimalCoincidence = minimalCoincidence;
        this.attributesCoefficientMap = attributesCoefficientMap;
    }

    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    @Override
    public int[] defineTrace(XLog xLog, XEvent xEvent) {
        Map<Integer, Float> coincidencesMap = buildCoincidenceMapForEvent(xLog, xEvent, attributesCoefficientMap);
        int [] traceIndexCoincidenceValue = getKeyOfBestMatchValue(coincidencesMap);
    }



    private Map<Integer, Float> buildCoincidenceMapForEvent(XLog xLog, XEvent xEvent, Map<String, Float> attributeCoefficientMap) {
        Map<Integer, Float> resultMap = new HashMap<>();
        for (int i = 0; i < xLog.size(); i++) {
            XTrace trace = xLog.get(i);
            XEvent lastEventOfTrace = trace.get(trace.size() - 1);
            float coincidenceValue = ValidationFactory.maxAttributesCoincidenceWeightCriteria(lastEventOfTrace, xEvent, attributeCoefficientMap);
            resultMap.put(i, coincidenceValue);
        }
        return resultMap;
    }

    private int [] getKeyOfBestMatchValue(Map<Integer, Float> coincidencesMap) {
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
