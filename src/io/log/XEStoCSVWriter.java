package io.log;

import io.FileUtils;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XEStoCSVWriter implements ILogWriter {

    /**
     * Date/Time parsing format including milliseconds and time zone
     * information.
     */
    private static final String XSDATETIME_FORMAT_STRING_MILLIS_TZONE = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String VALUES_SEPARATOR = ",";

    private String destination;
    private String name;

    public XEStoCSVWriter (){
        this.destination = DESTINATION_DIR;
        this.name = SimpleDateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
    }

    public XEStoCSVWriter (String destination, String name){
        this.destination = destination;
        this.name = name;
    }

    @Override
    public File write(XLog log) {
        return write(log, destination, name);
    }

    @Override
    public File write(XLog log, String fileName) {
        return write(log, destination, fileName);
    }

    @Override
    public File write(XLog log, String destDirectory, String destFileName) {
        File file = new File(destDirectory + destFileName + ".csv");
        file.getParentFile();
        try {

           if (file.exists()) {
               file.delete();
           }
           file.createNewFile();

           BufferedWriter writer = new BufferedWriter(new FileWriter(file));

           // Assume that we have a log with at least one trace which contains at least one event
           if (log.size() > 0 && log.get(0).size() > 0) {
               //Write header
               XAttributeMap xAttributeMap = log.get(0).get(0).getAttributes();
               writer.write(buildHeaderString(xAttributeMap));
               writer.newLine();

               // Write events
               for (int traceIndex = 0; traceIndex < log.size(); traceIndex++) {
                   for (XEvent xEvent : log.get(traceIndex)) {
                       StringBuilder stringBuilder = new StringBuilder();
                       stringBuilder.append(traceIndex);
                       stringBuilder.append(VALUES_SEPARATOR);
                       XAttributeMap attributes = xEvent.getAttributes();

                       for (String attrKey : xAttributeMap/*USE COMMON ATTRIBUTE MAP TO BE SURE THAT ORDER ALWAYS SAME*/.keySet()) {
                           XAttribute xAttribute = attributes.get(attrKey);
                           if (xAttribute != null) {
                               if (xAttribute instanceof XAttributeTimestampImpl) {
                                   String dateVal = new SimpleDateFormat(XSDATETIME_FORMAT_STRING_MILLIS_TZONE)
                                           .format(((XAttributeTimestampImpl) xAttribute).getValue());
                                   stringBuilder.append(dateVal);
                               } else {
                                   stringBuilder.append(xAttribute.toString());
                               }
                           }
                           stringBuilder.append(VALUES_SEPARATOR);
                       }
                       writer.write(stringBuilder.toString());
                       writer.newLine();
                       writer.flush();
                       stringBuilder.delete(0, stringBuilder.length());
                   }
               }
               writer.close();
           }
       } catch (Exception e){
           e.printStackTrace();
       }
        return file;
    }

    private String buildHeaderString(XAttributeMap xAttributeMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("traceId");
        stringBuffer.append(VALUES_SEPARATOR);
        for (String attrKey : xAttributeMap.keySet()) {
            stringBuffer.append(attrKey);
            stringBuffer.append(VALUES_SEPARATOR);
        }
        return stringBuffer.toString();
    }
}
