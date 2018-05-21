package algorithms.io;

import org.deckfour.xes.model.XLog;

import java.io.File;

public interface ILogWriter {
    File write(XLog log, String destDirectory, String fileName);
}
