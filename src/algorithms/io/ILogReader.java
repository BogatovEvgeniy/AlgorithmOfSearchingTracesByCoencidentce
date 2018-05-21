package algorithms.io;

import org.deckfour.xes.model.XLog;

import java.io.File;
import java.util.List;

public interface ILogReader {
    List<XLog> parse(File srcFile) throws Exception;
}
