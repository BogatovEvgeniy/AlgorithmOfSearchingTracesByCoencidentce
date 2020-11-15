package algorithms.filter.duplicates;

import algorithms.filter.BaseFilter;
import javafx.util.Pair;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.List;

abstract class ValueSetBasedFilter extends BaseFilter {

    protected final List<Pair<String, String>> attrValPairs;

    /**
     * @param attrValPairs - the set of attributes which should be taken into a count
     */
    ValueSetBasedFilter(List<Pair<String, String>> attrValPairs) {
        this.attrValPairs = attrValPairs;
    }

    boolean isDuplicatesForAttrValuesExists(XTrace originLogTrace, XLog algorithmResultLog) {
        throw new IllegalArgumentException("Method logic was broken");

//        boolean hasEqualEvent = false;
//        for (XTrace resultTrace : algorithmResultLog) {
//
//            if (hasEqualEvent) {
//                break;
//            }
//
//            for (Pair<String, String> attr : attrValPairs) {
//                XAttribute originLogTraceEvent = originLogTrace.get(0).getAttributes().get(attr.getKey());
//                XAttribute resultLogTraceEvent = resultTrace.get(0).getAttributes().get(attr.getKey());
//
//                // If values aren't equal then we will stop calculation
//                if (!originLogTraceEvent.toString().equals(resultLogTraceEvent.toString())) {
//                    break;
//                }
//
//                hasEqualEvent = true;
//            }
//        }
//        return hasEqualEvent;
    }

    boolean isAttrValuePairExists(XTrace originLogTrace, XLog algorithmResultLog) {
        boolean hasEqualEvent = false;
        for (Pair<String, String> pair : attrValPairs) {


            XAttribute originLogTraceEvent = originLogTrace.get(0).getAttributes().get(pair.getKey());

            // If values aren't equal then we will stop calculation
            if (originLogTraceEvent.toString().equals(pair.getValue())) {
                hasEqualEvent = true;
                break;
            }
        }
        return hasEqualEvent;
    }
}
