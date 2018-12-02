package io.log;

import org.deckfour.xes.model.XLog;

import java.io.File;
import java.io.IOException;

public interface ILogWriter {
    File write(XLog log, String destDirectory, String fileName) throws IOException;
}
