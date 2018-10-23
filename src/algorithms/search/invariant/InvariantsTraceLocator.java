package algorithms.search.invariant;

import algorithms.Utils;
import algorithms.preprocess.InvariantInitialEventSearchAlgorithm;
import algorithms.search.base.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.*;

import java.util.*;


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
            maxCoincidenceValueForAttr = getMaxCoincidenceValueForAttr(event, maxCoincidenceValueForAttr, compareEventData);

            traceCoincidenceMap.put(traceIndex, maxCoincidenceValueForAttr);
        }
        return sortResults(traceCoincidenceMap);
    }

    private float getMaxCoincidenceValueForAttr(XEvent event, float maxCoincidenceValueForAttr, CompareEventData compareEventData) {
        XAttributeMap getEventAttributes = event.getAttributes();
        for (String key : getEventAttributes.keySet()) {
            float coincidenceValue;
            int attrCoincidenceValue = 0;
            int comparedEvents = 0;

            XAttribute comparisionAttr = getEventAttributes.get(key);
            Node invariantNode = tree.getInvariantNodeForKey(comparisionAttr.getKey());
            Iterator invariantIterator = invariantNode.getAttributeInvariant().iterator();

            while (invariantIterator.hasNext()) {
                Object next = invariantIterator.next();
                Object attributeValue = compareEventData.inTraceValues.get(comparisionAttr).get(comparedEvents);
                if (next.equals(attributeValue)) {
                    attrCoincidenceValue++;
                    comparedEvents++;
                }
                break;
            }
            coincidenceValue = Math.floorDiv(attrCoincidenceValue, comparedEvents);

            if (coincidenceValue > maxCoincidenceValueForAttr) {
                maxCoincidenceValueForAttr = coincidenceValue;
            }
        }
        return maxCoincidenceValueForAttr;
    }

    private int[] sortResults(Map<Integer, Float> traceCoincidenceMap) {
        Map<Integer, Float> sortedMap = Utils.sortMap(traceCoincidenceMap);
        return Utils.toPrimitives(sortedMap.keySet());
    }


    /**
     * Get data of current event:
     * - Get event attributes
     * - Get values of each attribute of the event
     * - Put each value in eventValues map
     * <p>
     * Assume that all events in traces have the same list of attributes
     * Pass through event attributes for each event in trace
     * ---> Result: Set of values for each attribute for the trace
     *
     * @param event
     * @param trace
     * @return
     */
    private CompareEventData initCompareEventData(XEvent event, XTrace trace) {
        Map<XAttribute, String> currValues = new HashMap<>();
        Set<String> attrKeys = event.getAttributes().keySet();

        for (String key : attrKeys) {
            XAttribute xAttribute = event.getAttributes().get(key);
            String val = xAttribute.toString();
            currValues.put(xAttribute, val);
        }

        Map<XAttribute, List<String>> traceAttributesValues = new HashMap<>();
        for (String attrKey : attrKeys) {
            List<String> attrValues = new LinkedList<>();
            for (XEvent xEvent : trace) {
                attrValues.add(xEvent.getAttributes().get(attrKey).toString());
            }
            traceAttributesValues.put(event.getAttributes().get(attrKey), attrValues);
        }

        return new CompareEventData(currValues, traceAttributesValues);
    }


    @Override
    public ILogValidator getLogValidator() {
        if (LOG_VALIDATOR_INSTANCE == null) {
            LOG_VALIDATOR_INSTANCE = new InvariantLogValidator(tree);
        }
        return LOG_VALIDATOR_INSTANCE;
    }
}
