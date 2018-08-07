package algorithms.removal;

import io.ILogWriter;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.HashMap;
import java.util.Map;

public class TraceDuplicatesRemovingAlgorithm extends TraceTagRemovingAlgorithm {

    private ILogWriter logWriter;
    private final String[] attributesForComparision;

    /**
     * @param attributesForComparision - the set of attributes which should be taken into a count
     */
    public TraceDuplicatesRemovingAlgorithm(ILogWriter logWriter, String... attributesForComparision) {
        this.logWriter = logWriter;
        this.attributesForComparision = attributesForComparision;
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

    private boolean isDuplicatesForAttrValsExists(XTrace xTrace, XLog resLog) {
        Map<String, Boolean> results = new HashMap<>();
        for (XTrace trace : resLog) { // Contains duplicate
            if (results.values().contains(true)){
                break;
            }
            for (String attr : attributesForComparision) {
                XAttribute attrOfEventInLog = xTrace.get(0).getAttributes().get(attr);
                XAttribute attrOfCurrentEvent = trace.get(0).getAttributes().get(attr);

                if (attrOfEventInLog.toString().equals(attrOfCurrentEvent.toString())) {
                    results.put(attr, true);
                } else {
                    results.put(attr, false);
                }
            }
        }
        return results.values().contains(false);
    }
}
