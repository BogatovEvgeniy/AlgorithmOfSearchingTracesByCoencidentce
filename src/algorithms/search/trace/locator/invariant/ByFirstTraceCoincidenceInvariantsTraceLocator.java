package algorithms.search.trace.locator.invariant;

import algorithms.Utils;
import algorithms.preprocess.InvariantInitialEventSearchAlgorithm;
import algorithms.search.trace.ITraceSearchingAlgorithm;
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

        if (xLog.isEmpty()) {
            addEventInInvariantTree(0, event);
            return null;
        } else {
            int maxCoincidenceTrace = getMaxCoincidenceTraceIndexByAttr(event);

            if (maxCoincidenceTrace == NOT_INITIATED_INDEX) {
                addTraceInInvariantTree(event);
                return null;
            }

            addEventInInvariantTree(maxCoincidenceTrace, event);
            return new int [] {maxCoincidenceTrace};
        }
    }

    private void addEventInInvariantTree(int traceIndex, XEvent event) {
        for (String key : event.getAttributes().keySet()) {
            Node invariantNodeForKey = tree.getInvariantNodeForKey(key);
            if (invariantNodeForKey != null) {
                String eventVal = event.getAttributes().get(key).toString();
                invariantNodeForKey.addValue(traceIndex, eventVal);
            }
        }
    }

    private void addTraceInInvariantTree(XEvent event) {
        for (String key : event.getAttributes().keySet()) {
            Node invariantNodeForKey = tree.getInvariantNodeForKey(key);
            if (invariantNodeForKey != null) {
                String eventVal = event.getAttributes().get(key).toString();
                invariantNodeForKey.addTrace(eventVal);
            }
        }
    }

    @VisibleForTesting
    private int getMaxCoincidenceTraceIndexByAttr(XEvent event) {
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
            return sortedKeys[sortedKeys.length - 1];
        }
    }

    @VisibleForTesting
    private int defineFirstSuitableTraceIndex(String key, String eventVal) {

        Node invariantNode = tree.getInvariantNodeForKey(key);

        if (invariantNode == null) {
            return NOT_INITIATED_INDEX;
        }

        String previousInvariantVal = defineInvariantPreviousValue(eventVal, invariantNode);
        if (previousInvariantVal == null) {
            return NOT_INITIATED_INDEX;
        }

        int firstSuitableTrace = NOT_INITIATED_INDEX;
        if (invariantNode.getAttributeInvariant().contains(eventVal)) {
            return defineTraceWithSuitableValue(previousInvariantVal, invariantNode.getAllAvailableValues());
        } else {
            return setTraceIndexForValuesOutOfInvariant(invariantNode, firstSuitableTrace);
        }
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

    private int setTraceIndexForValuesOutOfInvariant(Node invariantNode, int firstSuitableTrace) {
        if (firstSuitableTrace == NOT_INITIATED_INDEX ) {
            int lastInsertionIndex = invariantNode.getLastInsertionIndex();
            List<String> traceValues= invariantNode.getAllAvailableValues().get(lastInsertionIndex);
            String lastValue = traceValues.get(traceValues.size() - 1);
            List<String> attributeInvariant = invariantNode.getAttributeInvariant();
            if (!lastValue.equals(attributeInvariant.get(attributeInvariant.size() - 1))) {
                firstSuitableTrace = lastInsertionIndex;
            } else {
                if(traceValues.size() > lastInsertionIndex) {
                    firstSuitableTrace = ++lastInsertionIndex;
                }
            }
        }
        return firstSuitableTrace;
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
