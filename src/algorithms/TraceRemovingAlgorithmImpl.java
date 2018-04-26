package algorithms;

import algorithms.ITraceRemovingAlgorithm;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
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
public class TraceRemovingAlgorithmImpl implements ITraceRemovingAlgorithm {

    protected static final String XSDATETIME_FORMAT_STRING_MILLIS_TZONE = "dd.MM.yyyyy HH:mm:ss";
    private File src;
    private File dest;

    public TraceRemovingAlgorithmImpl(File src, File dest) {
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

    private static XLog removalOfTraceSeparation(List<XLog> parsedLog) {
        XLog tracesRemoved = new XLogImpl(parsedLog.get(0).getAttributes());
        XTrace xTrace = (XTrace) parsedLog.get(0).get(0).clone();
        tracesRemoved.add(xTrace);
        for (int i = 1; i < parsedLog.get(0).size(); i++) {
            for (XEvent event : parsedLog.get(0).get(i)) {
                xTrace.add(event);
            }
        }
        return tracesRemoved;
    }

    private static XLog sortEventsByTimestamp(XLog xLog) {
        XTrace trace = xLog.get(0);
        Comparator<Object> comparator = new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                String timestampO1 = ((XEventImpl) o1).getAttributes().get("time:timestamp").toString();
                String timestampO2 = ((XEventImpl) o2).getAttributes().get("time:timestamp").toString();
                SimpleDateFormat dfMillisTZone = new SimpleDateFormat(XSDATETIME_FORMAT_STRING_MILLIS_TZONE);

                Date o1Date = null;
                Date o2Date = null;

                try {
                    o1Date = dfMillisTZone.parse(timestampO2);
                    o2Date = dfMillisTZone.parse(timestampO1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return o2Date.compareTo(o1Date);
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
