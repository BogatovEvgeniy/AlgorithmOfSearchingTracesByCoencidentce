package algorithms.filter.duplicates;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class RemoveTraceDuplicates extends DuplicatesBaseFilter {

    public RemoveTraceDuplicates(String... attributesForComparision) {
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
