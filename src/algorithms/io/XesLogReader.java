package algorithms.io;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.util.List;

public class XesLogReader implements ILogReader {

    public List<XLog> parse(File srcFile) throws Exception {
        // Delegate this work to reader class
        XesXmlParser xUniversalParser = new XesXmlParser();
        return xUniversalParser.parse(srcFile);
    }
}
