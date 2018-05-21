package algorithms.tracesearch.locators.coefficient;

import algorithms.ValidationFactory;
import algorithms.tracesearch.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.*;

public class CoefficientsTraceLocator implements ITraceSearchingAlgorithm.TraceLocator {
    private float minimalCoincidence;
    private Map<String, Float> attributesCoefficientMap;

    public CoefficientsTraceLocator(float minimalCoincidence, Map<String, Float> attributesCoefficientMap) {
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
        return getTracesIndexSortedByCoincidence(coincidencesMap);
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

    private int[] getTracesIndexSortedByCoincidence(Map<Integer, Float> coincidencesMap) {
        List<Integer> locatorResults = new LinkedList<>();

        Float[] coincidences = coincidencesMap.values().toArray(new Float[coincidencesMap.size()]);
        Arrays.sort(coincidences, Comparator.comparing(o -> ((Float) o)));

        for (int resIndex = 0; resIndex < coincidences.length; resIndex++) {
            if (coincidences[resIndex] >= minimalCoincidence) {
                locatorResults.add(getIndexByVal(coincidencesMap, coincidences[resIndex]));
            }
        }

        if (locatorResults.size() == 0) {
            return null;
        } else {
            return convertInPrimitives(locatorResults);
        }
    }

    private int[] convertInPrimitives(List<Integer> locatorResults) {
        int[] result = new int[locatorResults.size()];
        Arrays.fill(result, ITraceSearchingAlgorithm.TraceLocator.TRACE_INDEX_UNDEFINED_VALUE);
        for (int i = 0; i < locatorResults.size(); i++) {
            result[i] = locatorResults.get(i);
        }
        return result;
    }

    private int getIndexByVal(Map<Integer, Float> coincidencesMap, Float coincidence) {
        Iterator<Integer> iterator = coincidencesMap.keySet().iterator();
        Integer result = null;
        while (iterator.hasNext()) {
            Integer index = iterator.next();
            if (coincidencesMap.get(index).equals(coincidence)) {
                result = index;
            }
        }

        if (result == null)
            throw new IllegalStateException("Exceptional case. Wasn't found an trace with equals coincidence");
        return result;
    }


}
