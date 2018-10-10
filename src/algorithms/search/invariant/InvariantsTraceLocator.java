package algorithms.search.invariant;

import algorithms.Utils;
import algorithms.preprocess.InvariantInitialEventSearchAlgorithm;
import algorithms.search.base.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.*;


/**
 * While you are using this locator under the hood there is an assumption that log is prepeared
 * and that atleast one even of traces in log has attribute equalt to one of invariant
 * <p>
 * To prepare log for using this locator use {@link InvariantInitialEventSearchAlgorithm}
 */
public class InvariantsTraceLocator<V> implements ITraceSearchingAlgorithm.TraceLocator {

    private static ILogValidator LOG_VALIDATOR_INSTANCE;
    private AttributeInvariantTree<V> tree;

    public InvariantsTraceLocator(AttributeInvariantTree<V> tree) {
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

        // TODO Logic below was built on assumption that in case of there is at least one trace with at least one event into it
        Map<Integer, Float> traceCoincidenceMap = new HashMap<>();
        for (int traceIndex = 0; traceIndex < xLog.size(); traceIndex++) {
            XTrace currTrace = xLog.get(traceIndex);
            CompareEventData compareEventData = initCompareEventData(event, currTrace);
            float coincidenceValue = 0;
            float maxCoincidenceValueForAttr = 0;
            int attrCoincidenceValue = 0;
            int comparedEvents = 0;

            for (String key : event.getAttributes().keySet()) {
                XAttribute comparisionAttr = event.getAttributes().get(key);
                AttributeInvariantTree.Node<V> invariantNode = tree.getInvariantNodeForKey(comparisionAttr);
                Iterator<V> invariantIterator = invariantNode.getInvariantValues().iterator();

                // Here we checking coincidence value for of events values and values of each invariant
                // If value in an other invariant set will be more suitable then replace old attributeCoincidence value
                // for current tread by current attribute with new one due current tread still suitable according to another invariant
                while (invariantIterator.hasNext()) {
                    String next = invariantIterator.next();
                    List<String> values = next.getValues();

                    // Define val of coincidence for attribute value in invariant on I-place and value in the trace
                    for (int i = 0; i < values.size(); i++) {
                        if (values.get(i).equals(compareEventData.previousValues.get(comparisionAttr).get(i))) {
                            attrCoincidenceValue ++;
                            comparedEvents ++;
                        }
                        break;
                    }
                    coincidenceValue = attrCoincidenceValue / comparedEvents;

                    if (coincidenceValue > maxCoincidenceValueForAttr) {
                        maxCoincidenceValueForAttr = coincidenceValue;
                    }
                }
            }
            traceCoincidenceMap.put(traceIndex, maxCoincidenceValueForAttr);
        }

        return sortResults(traceCoincidenceMap);
    }

    private int[] sortResults(Map<Integer, Float> traceCoincidenceMap) {
        Map<Integer, Float> sortedMap = Utils.sortMap(traceCoincidenceMap);
        return Utils.toPrimitives(sortedMap.keySet());
    }


    private CompareEventData initCompareEventData(XEvent event, XTrace trace) {
        Map<XAttribute, String> currValues = new HashMap<>();
        Set<String> attrKeys = event.getAttributes().keySet();
        for (String key : attrKeys) {
            XAttribute xAttribute = event.getAttributes().get(key);
            String val = xAttribute.toString();
            currValues.put(xAttribute, val);
        }


        Map<XAttribute, List<String>> previousValues = new HashMap<>();
        for (String attrKey : attrKeys) {
            List<String> attrValues = new LinkedList<>();
            for (XEvent xEvent : trace) {
                attrValues.add(xEvent.getAttributes().get(attrKey).toString());
            }
            previousValues.put(event.getAttributes().get(attrKey), attrValues);
        }

        return new CompareEventData(currValues, previousValues);
    }


    @Override
    public ILogValidator getLogValidator() {
        if (LOG_VALIDATOR_INSTANCE == null) {
            LOG_VALIDATOR_INSTANCE = new InvariantLogValidator(attributeInvariantsTree);
        }
        return LOG_VALIDATOR_INSTANCE;
    }
}
