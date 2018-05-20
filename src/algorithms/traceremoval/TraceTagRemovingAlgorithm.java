package algorithms.traceremoval;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TraceTagRemovingAlgorithm implements ITraceRemovingAlgorithm {

    private File src;
    protected int traceCounter;

    public TraceTagRemovingAlgorithm(File srcFile) {
        this.src = srcFile;
    }

    @Override
    public XLog removeTraces() {
        List<XLog> parsedLog = null;
        XLog xLog = null;
        try {
            XesXmlParser xUniversalParser = new XesXmlParser();
            if (xUniversalParser.canParse(src)) {
                parsedLog = xUniversalParser.parse(src);
            }
            xLog = removalOfTraceTags(parsedLog);
            xLog = sortEventsByTimestamp(xLog);
        } catch (Exception e){
            e.printStackTrace();
        }
        return xLog;
    }

    private XLog removalOfTraceTags(List<XLog> parsedLog) {
        XLog tracesRemoved = new XLogImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class));
        XTrace xTrace = (XTrace) parsedLog.get(0).get(0).clone();
        tracesRemoved.add(xTrace);
        addEvent(parsedLog, xTrace);
        System.out.println("Traces added: " + traceCounter);
        return tracesRemoved;
    }

    protected void addEvent(List<XLog> parsedLog, XTrace xTrace) {
        for (int i = 1; i < parsedLog.get(0).size(); i++) {
            for (XEvent event : parsedLog.get(0).get(i)) {
                xTrace.add(event);
            }
        }
    }

    private static XLog sortEventsByTimestamp(XLog xLog) {
        XTrace trace = xLog.get(0);
        Comparator<Object> comparator = (o1, o2) -> {
            Date timestampO1 = ((XAttributeTimestamp)((XEventImpl) o1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP)).getValue();
            Date timestampO2 = ((XAttributeTimestamp)((XEventImpl) o2).getAttributes().get(XTimeExtension.KEY_TIMESTAMP)).getValue();
            return timestampO2.compareTo(timestampO1);
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
