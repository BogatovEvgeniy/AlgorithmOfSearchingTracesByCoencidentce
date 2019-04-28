package algorithms.filter.duplicates;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class GetTraceDuplicatesByAttribute extends DuplicatesBaseFilterByAttribute {

    public GetTraceDuplicatesByAttribute(String... attributesForComparision) {
        super(attributesForComparision);
    }

    @Override
    protected void addEvents(XLog originLog, XLog resLog) {
        for (XTrace trace : originLog) {
            if (isDuplicatesForAttrValsExists(trace, resLog)) {
                traceCounter++;
                resLog.add(trace);
            }
        }
    }
}
