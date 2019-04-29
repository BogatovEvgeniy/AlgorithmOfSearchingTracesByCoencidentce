package algorithms.filter;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

public class MergeEventsInOneTrace extends BaseFilter {

    protected void addEvents(XLog originLog, XLog resLog) {
        for (int traceIndex = 1; traceIndex < originLog.size(); traceIndex++) {
            for (XEvent event : originLog.get(traceIndex)) {
                resLog.get(0).add(event);
            }
        }
    }
}
