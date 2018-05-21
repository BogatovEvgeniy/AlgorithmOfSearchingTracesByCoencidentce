package algorithms.io;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XesLogWriter implements ILogWriter {

    public static final String EXTENSION = ".xes";

    @Override
    public File write(XLog log, String destDirectory, String fileName) {
        File file = new File(destDirectory + fileName + EXTENSION);
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
