package algorithms.filter;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class MergeEventsInOneTrace extends BaseFilter {

    protected void addEvents(XLog originLog, XLog resLog) {
        for (XTrace anOriginLog : originLog) {
            for (XEvent event : anOriginLog) {
                resLog.get(0).add(event);
            }
        }
    }
}
