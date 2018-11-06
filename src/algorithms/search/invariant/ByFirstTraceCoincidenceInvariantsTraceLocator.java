package algorithms.search.invariant;

import algorithms.Utils;
import algorithms.preprocess.InvariantInitialEventSearchAlgorithm;
import algorithms.search.base.ITraceSearchingAlgorithm;
import com.google.common.annotations.VisibleForTesting;
import org.deckfour.xes.model.*;

import javax.annotation.Nullable;
import java.util.*;


/**
 * While you are using this locator under the hood there is an assumption that log is prepeared
 * and that atleast one even of traces in log has attribute equalt to one of invariant
 * <p>
 * To prepare log for using this locator use {@link InvariantInitialEventSearchAlgorithm}
 */
public class ByFirstTraceCoincidenceInvariantsTraceLocator implements ITraceSearchingAlgorithm.TraceLocator {

    public static final int NOT_INITIATED_INDEX = -1;
    private static ILogValidator LOG_VALIDATOR_INSTANCE;
    private float minimalCoincidenceVal;
    private TraceInvariantList tree;

    public ByFirstTraceCoincidenceInvariantsTraceLocator(float minimalCoincidenceVal, TraceInvariantList tree) {
        this.minimalCoincidenceVal = minimalCoincidenceVal;
        this.tree = tree;
    }

    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    @VisibleForTesting
    @Override
    public int[] defineSuitableTracesList(XLog xLog, XEvent event) {

        Map<Integer, Float> traceCoincidenceMap;
        if (xLog.isEmpty()) {
            for (String key : event.getAttributes().keySet()) {
                Node invariantNodeForKey = tree.getInvariantNodeForKey(key);
                if (invariantNodeForKey != null) {
                    String eventVal = event.getAttributes().get(key).toString();
                    invariantNodeForKey.addTrace(eventVal);
                }
            }
            return null;
        } else {
            for (int traceIndex = 0; traceIndex < xLog.size(); traceIndex++) {
                float maxCoincidenceValueForAttr = getMaxCoincidenceByAttr(event);
                if (maxCoincidenceValueForAttr > minimalCoincidenceVal) {
                    traceCoincidenceMap = new HashMap<>();
                    traceCoincidenceMap.put(traceIndex, maxCoincidenceValueForAttr);
                    return sortIndexesByValues(traceCoincidenceMap);
                } else {
                    return null;
                }
            }
            return null;
        }
    }

    @VisibleForTesting
    private float getMaxCoincidenceByAttr(XEvent event) {
        XAttributeMap getEventAttributes = event.getAttributes();
        Map<Integer, Float> traceAttributesCoincidenceValues = new HashMap<>();
        for (String key : getEventAttributes.keySet()) {
            String eventVal = event.getAttributes().get(key).toString();
            int firsSuitableTraceIndex = defineFirstSuitableTraceIndex(key, eventVal);
            if (firsSuitableTraceIndex != NOT_INITIATED_INDEX) {
                Float value = traceAttributesCoincidenceValues.get(firsSuitableTraceIndex);
                if (value != null) {
                    value++;
                } else {
                    value = 1F;
                }
                traceAttributesCoincidenceValues.put(firsSuitableTraceIndex, value);
            }
        }

        if (traceAttributesCoincidenceValues.isEmpty()) {
            return NOT_INITIATED_INDEX;
        } else {
            int[] sortedKeys = sortIndexesByValues(traceAttributesCoincidenceValues);
            int maxCoincidencePos = sortedKeys.length - 1;
            return (float) sortedKeys[maxCoincidencePos] / getEventAttributes.keySet().size();
        }
    }

    @VisibleForTesting
    private int defineFirstSuitableTraceIndex(String key, String eventVal) {
        long attrCoincidenceValue = 0;

        Node invariantNode = tree.getInvariantNodeForKey(key);

        if (invariantNode == null) {
            return 0;
        }

        String previousInvariantVal = defineInvariantPreviousValue(eventVal, invariantNode);
        if (previousInvariantVal == null) {
            invariantNode.addTrace(eventVal);
            return 0;
        }

        int firstSuitableTrace = defineTraceWithSuitableValue(previousInvariantVal, invariantNode.getAllAvailableValues());

        if (firstSuitableTrace == NOT_INITIATED_INDEX) {
            invariantNode.addTrace(eventVal);
        } else {
            invariantNode.addValue(firstSuitableTrace, eventVal);
        }

        return firstSuitableTrace;
    }

    private int defineTraceWithSuitableValue(String previousInvariantVal, List<List<String>> allAvailableValues) {
        int suitableTraceIndex = NOT_INITIATED_INDEX;

        for (int traceIndex = 0; traceIndex < allAvailableValues.size(); traceIndex++) {
            int traceSize = allAvailableValues.get(traceIndex).size();

            String lastValueInTrace = allAvailableValues.get(traceIndex).get(traceSize - 1);
            if (lastValueInTrace.equals(previousInvariantVal)) {
                suitableTraceIndex = traceIndex;
                break;
            }
        }
        return suitableTraceIndex;
    }

    @Nullable
    private String defineInvariantPreviousValue(String eventVal, Node invariantNode) {
        String previousInvariantVal = null;
        Iterator<String> invariantIterator = invariantNode.getAttributeInvariant().iterator();
        while (invariantIterator.hasNext()) {
            String next = invariantIterator.next();
            if (next.equals(eventVal)) {
                break;
            } else {
                previousInvariantVal = next;
                continue;
            }
        }
        return previousInvariantVal;
    }

    @VisibleForTesting
    private int[] sortIndexesByValues(Map<Integer, Float> traceCoincidenceMap) {
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
