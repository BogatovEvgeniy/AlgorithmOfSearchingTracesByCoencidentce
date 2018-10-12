package algorithms.search.invariant;

import org.deckfour.xes.model.XAttribute;

import java.util.List;
import java.util.Map;

public class CompareEventData {

    Map<XAttribute, String> eventValues;
    Map<XAttribute, List<String>> inTraceValues;

    public CompareEventData(Map<XAttribute, String> eventValues, Map<XAttribute, List<String>> inTraceValues) {
        this.eventValues = eventValues;
        this.inTraceValues = inTraceValues;
    }
}
