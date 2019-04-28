package algorithms.filter.duplicates;

import algorithms.filter.BaseFilter;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

class DuplicatesBaseFilterByAttribute extends BaseFilter {

    private final String[] attributesForComparision;

    /**
     * @param attributesForComparision - the set of attributes which should be taken into a count
     */
    DuplicatesBaseFilterByAttribute(String... attributesForComparision) {
        this.attributesForComparision = attributesForComparision;
    }

    boolean isDuplicatesForAttrValsExists(XTrace originLogTrace, XLog algorithmResultLog) {
        boolean hasEqualEvent = false;

        for (XTrace resultTrace : algorithmResultLog) {

            // If some difference duplicate
            if (hasEqualEvent) {
                break;
            }

            for (String attr : attributesForComparision) {
                XAttribute originLogTraceEvent = originLogTrace.get(0).getAttributes().get(attr);
                XAttribute resultLogTraceEvent = resultTrace.get(0).getAttributes().get(attr);

                // If values aren't equal then we will stop calculation
                if (!originLogTraceEvent.toString().equals(resultLogTraceEvent.toString())) {
                    break;
                }

                hasEqualEvent = true;
            }
        }
        return hasEqualEvent;
    }
}
