package algorithms.traceremoval;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.File;
import java.util.List;

public class ParallelTraceTagRemovingAlgorithm extends TraceTagRemovingAlgorithm {

    private final String[] attributesForComparision;

    /**
     * @param src - source file, the file where initial log is stored
     * @param attributesForComparision - the set of attributes which should be taken into a count
     */
    public ParallelTraceTagRemovingAlgorithm(File src, String ... attributesForComparision) {
        super(src);
        this.attributesForComparision = attributesForComparision;
    }

    @Override
    protected void addEvent(List<XLog> parsedLog, XTrace xTrace) {
        for (int i = 1; i < parsedLog.get(0).size(); i++) {
            if (!traceContainsEventsWithSameTraceProduct(xTrace, parsedLog.get(0).get(i))) {
                traceCounter++;
                for (XEvent event : parsedLog.get(0).get(i)) {
                    xTrace.add(event);
                }
            }
        }
    }

    private boolean traceContainsEventsWithSameTraceProduct(XTrace xTrace, XTrace currEvent) {
        for (XEvent xEvent : xTrace) {
            for (String attr : attributesForComparision) {
                XAttribute attrOfEventInLog = xEvent.getAttributes().get(attr);
                XAttribute attrOfCurrentEvent = currEvent.get(0).getAttributes().get(attr);

                if(!attrOfEventInLog.toString().equals(attrOfCurrentEvent.toString())) {
                    break;
                }
                return true;
            }
        }
        return false;
    }
}
