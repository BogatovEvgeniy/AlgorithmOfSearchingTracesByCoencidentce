package algorithms.filter.duplicates;

import org.deckfour.xes.model.XLog;

public class GetTraceDuplicatesByAttribute extends DuplicatesBaseFilterByAttribute {

    public GetTraceDuplicatesByAttribute(String... attributesForComparision) {
        super(attributesForComparision);
    }

    @Override
    protected void addEvents(XLog originLog, XLog resLog) {
        for (int traceIndex = 1; traceIndex < originLog.size(); traceIndex++) {
            if (isDuplicatesForAttrValsExists(originLog.get(traceIndex), resLog)) {
                traceCounter++;
                resLog.add(originLog.get(traceIndex));
            }
        }
    }
}
