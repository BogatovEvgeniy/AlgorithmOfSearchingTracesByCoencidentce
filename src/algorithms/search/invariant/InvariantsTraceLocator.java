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
            float coincidenceValue = defineCoincidenceByInvariant(key, compareEventData);
            if (coincidenceValue > maxCoincidenceValueForAttr) {
                maxCoincidenceValueForAttr = coincidenceValue;
            }
        }
        return maxCoincidenceValueForAttr;
    }

    @VisibleForTesting
    private float defineCoincidenceByInvariant(String key, CompareEventData compareEventData) {
        float coincidenceValue;
        int comparedEvents = 0;
        long attrCoincidenceValue = 0;

        Node invariantNode = tree.getInvariantNodeForKey(key);
        Iterator invariantIterator = invariantNode.getAttributeInvariant().iterator();

        while (invariantIterator.hasNext()) {
            Object next = invariantIterator.next();
            Object attributeValue = compareEventData.inTraceValues.get(key).get(comparedEvents);
            if (next.equals(attributeValue)) {
                attrCoincidenceValue++;
                comparedEvents++;
            }
            break;
        }

        coincidenceValue = Math.floorDiv(attrCoincidenceValue, comparedEvents);
        return coincidenceValue;
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
