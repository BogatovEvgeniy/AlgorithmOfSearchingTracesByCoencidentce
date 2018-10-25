package algorithms.search.invariant;

import algorithms.Utils;
import algorithms.preprocess.InvariantInitialEventSearchAlgorithm;
import algorithms.search.base.ITraceSearchingAlgorithm;
import com.google.common.annotations.VisibleForTesting;
import org.deckfour.xes.model.*;

import java.util.*;

import static algorithms.search.invariant.CompareEventData.initCompareEventData;


/**
 * While you are using this locator under the hood there is an assumption that log is prepeared
 * and that atleast one even of traces in log has attribute equalt to one of invariant
 * <p>
 * To prepare log for using this locator use {@link InvariantInitialEventSearchAlgorithm}
 */
public class InvariantsTraceLocator implements ITraceSearchingAlgorithm.TraceLocator {

    private static ILogValidator LOG_VALIDATOR_INSTANCE;
    private TraceInvariantList tree;

    public InvariantsTraceLocator(TraceInvariantList tree) {
        this.tree = tree;
    }

    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    @VisibleForTesting
    @Override
    public int[] defineSuitableTracesList(XLog xLog, XEvent event) {

        if (xLog.isEmpty()) {
            return null;
        }

        Map<Integer, Float> traceCoincidenceMap = new HashMap<>();

        for (int traceIndex = 0; traceIndex < xLog.size(); traceIndex++) {
            float maxCoincidenceValueForAttr = 0;
            XTrace currTrace = xLog.get(traceIndex);
            CompareEventData compareEventData = initCompareEventData(event, currTrace);
            maxCoincidenceValueForAttr = getMaxCoincidenceValueForEventByAttributes(event, maxCoincidenceValueForAttr, compareEventData);
            traceCoincidenceMap.put(traceIndex, maxCoincidenceValueForAttr);
        }
        return sortResults(traceCoincidenceMap);
    }

    @VisibleForTesting
    private float getMaxCoincidenceValueForEventByAttributes(XEvent event, float maxCoincidenceValueForAttr, CompareEventData compareEventData) {
        XAttributeMap getEventAttributes = event.getAttributes();
        for (String key : getEventAttributes.keySet()) {
            float coincidenceValue = defineCoincidenceByInvariant(key, event);
            if (coincidenceValue > maxCoincidenceValueForAttr) {
                maxCoincidenceValueForAttr = coincidenceValue;
            }
        }
        return maxCoincidenceValueForAttr;
    }

    @VisibleForTesting
    private float defineCoincidenceByInvariant(String key, XEvent xEvent) {
        float coincidenceValue;
        int comparedEvents = 0;
        long attrCoincidenceValue = 0;

        Node invariantNode = tree.getInvariantNodeForKey(key);

        if (invariantNode == null) {
            return 0;
        }

        Iterator<String> invariantIterator = invariantNode.getAttributeInvariant().iterator();
        List<String> passedValues = new LinkedList<>();
        while (invariantIterator.hasNext()) {
            String next = invariantIterator.next();
            String attributeValue = xEvent.getAttributes().get(key).toString();
            if (next.equals(attributeValue)) {
                Integer traceWithTheBestCoincidence = getTraceWithTheBestCoincidence(invariantNode.getAllAvailableValues(), passedValues, attributeValue);
                invariantNode.addValue(traceWithTheBestCoincidence, next);
            } else {
                passedValues.add(next);
            }
            break;
        }

        System.out.println(invariantNode.toString());
        System.out.println("Attr coins " + attrCoincidenceValue + " comparedEvents " + comparedEvents);

        coincidenceValue = Math.floorDiv(attrCoincidenceValue, comparedEvents);
        return coincidenceValue;
    }

    private Integer getTraceWithTheBestCoincidence(List<List<String>> allAvailableValues, List<String> passedValues, String currentEventVal) {
        List<Integer> traceCoincidence = new ArrayList<>(passedValues.size());
        if (passedValues.size() >0) {
            for (int i = passedValues.size() - 1; i >= 0; i--) {
                String valueForSearch = passedValues.get(i);
                getTracesCoincidenceList(allAvailableValues, traceCoincidence, valueForSearch);
            }
        } else {
            getTracesCoincidenceList(allAvailableValues, traceCoincidence, currentEventVal);
        }

        return traceCoincidence.size() == 0 ? 0 : Collections.max(traceCoincidence);
    }

    private void getTracesCoincidenceList(List<List<String>> allAvailableValues, List<Integer> traceCoincidence, String valueForSearch) {
        for (int traceIndex = allAvailableValues.size() - 1; traceIndex >= 0; traceIndex--) {
            for (int eventIndex = allAvailableValues.get(traceIndex).size() - 1; eventIndex >= 0; eventIndex--) {
                String event = allAvailableValues.get(traceIndex).get(eventIndex);
                if (event.equals(valueForSearch)) {
                    int traceCoincidenceVal = traceCoincidence.get(traceIndex);
                    traceCoincidence.add(traceIndex, traceCoincidenceVal);
                } else if (eventIndex > 0) {
                    continue;
                } else {
                    allAvailableValues.remove(traceIndex);
                }
            }
        }
    }

    @VisibleForTesting
    private int[] sortResults(Map<Integer, Float> traceCoincidenceMap) {
        Map<Integer, Float> sortedMap = Utils.sortMap(traceCoincidenceMap);
        return Utils.toPrimitives(sortedMap.keySet());
    }

    @Override
    public ILogValidator getLogValidator() {
        if (LOG_VALIDATOR_INSTANCE == null) {
            LOG_VALIDATOR_INSTANCE = new InvariantLogValidator(tree);
        }
        return LOG_VALIDATOR_INSTANCE;
    }
}
