package algorithms.traceremoval;

import algorithms.ILogAlgorithm;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class TraceTagRemovingAlgorithm implements ILogAlgorithm {

    protected XLog originLog;
    protected int traceCounter;

    @Override
    public XLog proceed(XLog originLog) {
        this.originLog = originLog;
        XLog xLog = removalOfTraceTags(originLog);
        xLog = sortEventsByTimestamp(xLog);
        return xLog;
    }

    protected XLog removalOfTraceTags(XLog originLog) {
        XLog tracesRemoved = new XLogImpl(new XAttributeMapLazyImpl<>(XAttributeMapImpl.class));
        XTrace xTrace = (XTrace) originLog.get(0).clone();
        tracesRemoved.add(xTrace);
        addEvent(originLog, xTrace);
        System.out.println("Traces added: " + traceCounter);
        return tracesRemoved;
    }

    protected void addEvent(XLog originLog, XTrace xTrace) {
        for (int i = 1; i < originLog.size(); i++) {
            for (XEvent event : originLog.get(i)) {
                xTrace.add(event);
            }
        }
    }

    protected static XLog sortEventsByTimestamp(XLog xLog) {
        XTrace trace = xLog.get(0);
        Comparator<Object> comparator = (o1, o2) -> {
            Date timestampO1 = ((XAttributeTimestamp) ((XEventImpl) o1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP)).getValue();
            Date timestampO2 = ((XAttributeTimestamp) ((XEventImpl) o2).getAttributes().get(XTimeExtension.KEY_TIMESTAMP)).getValue();
            return timestampO1.compareTo(timestampO2);
        };

        Object[] events = trace.toArray();
        Arrays.sort(events, comparator);
        trace.clear();
        for (Object event : events) {
            trace.add((XEventImpl) event);
        }

        return xLog;
    }
}
