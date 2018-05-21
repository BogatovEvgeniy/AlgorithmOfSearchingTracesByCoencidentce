package algorithms.traceremoval;

import io.ILogWriter;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class ParallelTraceTagRemovingAlgorithm extends TraceTagRemovingAlgorithm {

    private ILogWriter logWriter;
    private final String[] attributesForComparision;

    /**
     * @param attributesForComparision - the set of attributes which should be taken into a count
     */
    public ParallelTraceTagRemovingAlgorithm(ILogWriter logWriter, String... attributesForComparision) {
        this.logWriter = logWriter;
        this.attributesForComparision = attributesForComparision;
    }

    @Override
    protected void addEvent(XLog originLog, XTrace xTrace) {
        for (int i = 1; i < originLog.size(); i++) {
            if (!traceContainsEventsWithSameTraceProduct(xTrace, originLog.get(i))) {
                traceCounter++;
                for (XEvent event : originLog.get(i)) {
                    xTrace.add(event);
                }
            }
        }
    }

    @Override
    public XLog proceed(XLog originLog) {
        this.originLog = originLog;
        XLog xLog = removalOfTraceTags(originLog);
        logWriter.write(xLog, "C:\\Users\\ievgen_bogatov\\Desktop\\", "WithoutParallelBP");
        xLog = sortEventsByTimestamp(xLog);
        return xLog;
    }

    private boolean traceContainsEventsWithSameTraceProduct(XTrace xTrace, XTrace currEvent) {
        for (XEvent xEvent : xTrace) {
            for (String attr : attributesForComparision) {
                XAttribute attrOfEventInLog = xEvent.getAttributes().get(attr);
                XAttribute attrOfCurrentEvent = currEvent.get(0).getAttributes().get(attr);

                if (!attrOfEventInLog.toString().equals(attrOfCurrentEvent.toString())) {
                    break;
                }
                return true;
            }
        }
        return false;
    }
}
