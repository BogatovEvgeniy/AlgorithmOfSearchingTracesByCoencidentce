package io;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class XEStoCSVWriter implements ILogWriter {

    @Override
    public File write(XLog log, String destDirectory, String destFileName) throws IOException {
        File file = new File(destDirectory + destFileName + ".txt");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));

        // Assume that we have a log with at least one trace which contains at least one event
        if (log.size() > 0 && log.get(0).size() > 0) {
            //Write header
            XAttributeMap xAttributeMap = log.get(0).get(0).getAttributes();
            writer.write(buildHeaderString(xAttributeMap));
            writer.write("\n");

            // Write events
            for (int traceIndex = 0; traceIndex < log.size(); traceIndex++) {
                for (XEvent xEvent : log.get(traceIndex)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(traceIndex);
                    stringBuilder.append(";");
                    XAttributeMap attributes = xEvent.getAttributes();

                    for (String attrKey : xAttributeMap/*USE COMMON ATTRIBUTE MAP TO BE SURE THAT ORDER ALWAYS SAME*/.keySet()) {
                        stringBuilder.append(attributes.get(attrKey).toString());
                        stringBuilder.append(";");
                    }
                    writer.write(stringBuilder.toString());
                    writer.write("\n");
                    stringBuilder.delete(0,stringBuilder.length());
                }
            }
            writer.close();
        }
        return file;
    }

    private String buildHeaderString(XAttributeMap xAttributeMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("traceId");
        stringBuffer.append(";");
        for (String attrKey : xAttributeMap.keySet()) {
            stringBuffer.append(attrKey);
            stringBuffer.append(";");
        }
        return stringBuffer.toString();
    }
}
