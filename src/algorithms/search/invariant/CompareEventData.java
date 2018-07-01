package algorithms.search.invariant;

import org.deckfour.xes.model.XAttribute;

import java.util.List;
import java.util.Map;

public class CompareEventData {

    Map<XAttribute, String> currentValues;
    Map<XAttribute, List<String>> previousValues;

    public CompareEventData(Map<XAttribute, String> currentValues, Map<XAttribute, List<String>> previousValues) {
        this.currentValues = currentValues;
        this.previousValues = previousValues;
    }
}
