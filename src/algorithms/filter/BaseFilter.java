package algorithms.filter;

import algorithms.ILogAlgorithm;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * 1. Algorithm initiates a log with an empty first trace of a log
 * 2. Adds events. Behaviour of this step can be overwritten in a child class
 * 3. Sorts all events in a trace by time, will be executed for all events in each trace
 */
public abstract class BaseFilter implements ILogAlgorithm<XLog> {

    protected XLog originLog;
    protected int traceCounter;
    public static final XAttributeMapLazyImpl<XAttributeMapImpl> EMPTY_ATTRIBUTES = new XAttributeMapLazyImpl<>(XAttributeMapImpl.class);

    @Override
    public XLog proceed(XLog originLog) {
        this.originLog = originLog;
        XLog xLog = new XLogImpl(EMPTY_ATTRIBUTES);
        addClearTraceInLog(xLog);

        // add events of the first trace
        xLog.get(0).addAll(originLog.get(0));

        //Start from second ite due first were added above
        addEvents(originLog, xLog);

        System.out.println("Traces added: " + traceCounter);
        xLog = sortEventsByTimestamp(xLog);
        return xLog;
    }

    @Override
    public String getResultsName() {
        return this.getClass().getSimpleName();
    }

    protected void addClearTraceInLog(XLog clearLog) {
        XTrace trace = new XTraceImpl(new XAttributeMapLazyImpl<>(XAttributeMapImpl.class));
        clearLog.add(removeTraceAttributes(trace));
    }

    private XTrace removeTraceAttributes(XTrace trace) {
        XTrace clearTrace = (XTrace) trace.clone();
        clearTrace.setAttributes(EMPTY_ATTRIBUTES);
        return clearTrace;
    }

    protected abstract void addEvents(XLog originLog, XLog resLog);

    protected static XLog sortEventsByTimestamp(XLog xLog) {
        for (XTrace trace : xLog) {
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
        }
        return xLog;
    }
}
