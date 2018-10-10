package algorithms.removal;

import algorithms.ILogAlgorithm;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class MergeEventsInOneTraceAndTraseTagsRemovingAlgorithm implements ILogAlgorithm {

    protected XLog originLog;
    protected int traceCounter;
    public static final XAttributeMapLazyImpl<XAttributeMapImpl> EMPTY_ATTRIBUTES = new XAttributeMapLazyImpl<>(XAttributeMapImpl.class);

    @Override
    public XLog proceed(XLog originLog) {
        this.originLog = originLog;
        XLog xLog = getLogWithFirstClearTrace(originLog);
        //Start from second ite due first were added above
        addEvents(originLog, xLog);
        System.out.println("Traces added: " + traceCounter);
        xLog = sortEventsByTimestamp(xLog);
        return xLog;
    }

    protected XLog getLogWithFirstClearTrace(XLog originLog) {
        XLog clearLog = new XLogImpl(EMPTY_ATTRIBUTES);
        XTrace trace = originLog.get(0);
        clearLog.add(traceWithoutTags(trace));
        return clearLog;
    }

    private XTrace traceWithoutTags(XTrace trace) {
        XTrace clearTrace = (XTrace) trace.clone();
        clearTrace.setAttributes(EMPTY_ATTRIBUTES);
        return clearTrace;
    }

    protected void addEvents(XLog originLog, XLog resLog) {
        for (int i = 1; i < originLog.size(); i++) {
            resLog.add(traceWithoutTags(originLog.get(i)));
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
