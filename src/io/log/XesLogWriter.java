package io.log;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XesLogWriter implements ILogWriter {

    public static final String EXTENSION = ".xes";
    private final String destination;
    private final String name;

    public XesLogWriter (){
        this.destination = DESTINATION_DIR;
        this.name = SimpleDateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
    }


    public XesLogWriter(String destination, String name) {
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
    public File write(XLog log, String destDirectory, String fileName) {
        File file = new File(destDirectory + fileName + EXTENSION);
        file.getParentFile().mkdirs();
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            new XesXmlSerializer().serialize(log, new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
