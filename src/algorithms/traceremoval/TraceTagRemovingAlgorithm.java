package algorithms.traceremoval;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.*;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Ievgen_Bogatov on 09.04.2018.
 */
public class TraceTagRemovingAlgorithm implements ITraceRemovingAlgorithm {

    private File src;
    private File dest;
    protected int traceCounter;

    public TraceTagRemovingAlgorithm(File src, File dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public void removeTraces() {
        try {
            XesXmlParser xUniversalParser = new XesXmlParser();
            List<XLog> parsedLog = null;
            if (xUniversalParser.canParse(src)) {
                parsedLog = xUniversalParser.parse(src);
            }
            XLog xLog = removalOfTraceSeparation(parsedLog);
            xLog = sortEventsByTimestamp(xLog);
            new XesXmlSerializer().serialize(xLog, new FileOutputStream(dest));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private XLog removalOfTraceSeparation(List<XLog> parsedLog) {
        XLog tracesRemoved = new XLogImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class));
        XTrace xTrace = (XTrace) parsedLog.get(0).get(0).clone();
        tracesRemoved.add(xTrace);
        addEvent(parsedLog, xTrace);
        System.out.println("Traces added: " + traceCounter);
        return tracesRemoved;
    }

    protected void addEvent(List<XLog> parsedLog, XTrace xTrace) {
        for (int i = 1; i < parsedLog.get(0).size(); i++) {
            for (int eventIndex = 0; eventIndex< parsedLog.get(0).get(i).size(); eventIndex++) {
                xTrace.add(parsedLog.get(0).get(i).get(eventIndex));
            }
        }
    }

    private static XLog sortEventsByTimestamp(XLog xLog) {
        XTrace trace = xLog.get(0);
        Comparator<Object> comparator = new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                Date timestampO1 = ((XAttributeTimestamp)((XEventImpl) o1).getAttributes().get("time:timestamp")).getValue();
                Date timestampO2 = ((XAttributeTimestamp)((XEventImpl) o2).getAttributes().get("time:timestamp")).getValue();
                return timestampO2.compareTo(timestampO1);
            }
        };

        Object[] a = trace.toArray();
        Arrays.sort(a, comparator);
        trace.clear();
        for (Object o : a) {
            trace.add((XEventImpl) o);
        }

        return xLog;
    }

}
