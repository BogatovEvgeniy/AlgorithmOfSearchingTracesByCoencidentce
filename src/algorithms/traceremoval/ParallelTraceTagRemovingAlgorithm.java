package algorithms.traceremoval;

import algorithms.traceremoval.TraceTagRemovingAlgorithm;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.File;
import java.util.List;

public class ParallelTraceTagRemovingAlgorithm extends TraceTagRemovingAlgorithm {

    public ParallelTraceTagRemovingAlgorithm(File src, File dest) {
        super(src, dest);
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
            XAttribute xAttribute = xEvent.getAttributes().get("product");
            XAttribute product = currEvent.get(0).getAttributes().get("product");
            if(xAttribute.toString().equals(product.toString())) {
               return true;
           }
        }
        return false;
    }
}
