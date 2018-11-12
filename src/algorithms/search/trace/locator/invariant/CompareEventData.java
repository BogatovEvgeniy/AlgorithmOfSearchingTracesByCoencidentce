package algorithms.search.trace.locator.invariant;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.util.*;

public class CompareEventData {

    public Map<String, String> eventValues;
    public Map<String, List<String>> inTraceValues;

    public CompareEventData(Map<String, String> eventValues, Map<String, List<String>> inTraceValues) {
        this.eventValues = eventValues;
        this.inTraceValues = inTraceValues;
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
    public static CompareEventData initCompareEventData(XEvent event, XTrace trace) {
        Map<String, String> currValues = new HashMap<>();
        Set<String> attrKeys = event.getAttributes().keySet();

        for (String key : attrKeys) {
            XAttribute xAttribute = event.getAttributes().get(key);
            String val = xAttribute.toString();
            currValues.put(key, val);
        }

        Map<String, List<String>> traceAttributesValues = new HashMap<>();
        for (String attrKey : attrKeys) {
            List<String> attrValues = new LinkedList<>();
            for (XEvent xEvent : trace) {
                attrValues.add(xEvent.getAttributes().get(attrKey).toString());
            }
            traceAttributesValues.put(attrKey, attrValues);
        }

        return new CompareEventData(currValues, traceAttributesValues);
    }
}
