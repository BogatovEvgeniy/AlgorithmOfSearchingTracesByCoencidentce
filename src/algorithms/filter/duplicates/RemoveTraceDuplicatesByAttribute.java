package algorithms.filter.duplicates;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class RemoveTraceDuplicatesByAttribute extends DuplicatesBaseFilterByAttribute {

    public RemoveTraceDuplicatesByAttribute(String... attributesForComparision) {
        super(attributesForComparision);
    }

    @Override
    protected void addEvents(XLog originLog, XLog algorithmResultLog) {
        for (XTrace originLogTrace : originLog) {
            if (!isDuplicatesForAttrValsExists(originLogTrace, algorithmResultLog)) {
                traceCounter++;
                algorithmResultLog.add(originLogTrace);
            }
        }
    }
}
